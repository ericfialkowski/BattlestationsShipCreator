package com.ericski.Battlestations;

import static com.ericski.Battlestations.BattlestationColors.Combat;
import static com.ericski.Battlestations.BattlestationColors.Piloting;
import static com.ericski.Battlestations.BattlestationColors.Science;
import static com.ericski.Battlestations.RuleSetEnum.Version1;
import static com.ericski.Battlestations.RuleSetVersion.getInstance;
import static com.ericski.Battlestations.ShipFactory.INSTANCE;
import static com.ericski.graphics.BetterImageScaler.getFasterScaledInstance;
import com.lowagie.text.Document;
import com.lowagie.text.DocumentException;
import static com.lowagie.text.Element.ALIGN_CENTER;
import static com.lowagie.text.Element.ALIGN_LEFT;
import static com.lowagie.text.Font.BOLD;
import static com.lowagie.text.Font.BOLDITALIC;
import static com.lowagie.text.Font.NORMAL;
import static com.lowagie.text.Font.UNDERLINE;
import static com.lowagie.text.FontFactory.HELVETICA;
import static com.lowagie.text.FontFactory.getFont;
import static com.lowagie.text.Image.MIDDLE;
import static com.lowagie.text.Image.getInstance;
import static com.lowagie.text.PageSize.LEGAL;
import static com.lowagie.text.PageSize.LETTER;
import com.lowagie.text.Paragraph;
import com.lowagie.text.Rectangle;
import static com.lowagie.text.Rectangle.BOTTOM;
import static com.lowagie.text.Rectangle.BOX;
import static com.lowagie.text.Rectangle.LEFT;
import static com.lowagie.text.Rectangle.NO_BORDER;
import static com.lowagie.text.Rectangle.RIGHT;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;
import static com.lowagie.text.pdf.PdfWriter.getInstance;
import java.awt.Color;
import static java.awt.Color.BLACK;
import static java.awt.Color.BLUE;
import static java.awt.Color.CYAN;
import static java.awt.Color.GRAY;
import static java.awt.Color.MAGENTA;
import static java.awt.Color.WHITE;
import static java.awt.RenderingHints.VALUE_INTERPOLATION_BICUBIC;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import static java.lang.Math.max;
import static java.lang.System.out;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import static javax.imageio.ImageIO.read;
import static org.apache.logging.log4j.LogManager.getLogger;
import org.apache.logging.log4j.Logger;

public class PDFShipWriter
{

	static final Logger logger = getLogger(PDFShipWriter.class);

	public static void drawPDF(Ship ship, OutputStream outStream)
		throws IOException
	{
		drawPDF(ship, outStream, new PDFShipWriterOptions());
	}

	public static void drawPDF(List<Ship> ships, OutputStream outStream,
							   PDFShipWriterOptions options) throws IOException
	{
		BufferedImage logoImage = null;
		InputStream imageStream = Ship.class
			.getResourceAsStream("/com/ericski/Battlestations/Images/batstationlogo.gif");
		try
		{
			logoImage = read(imageStream);
		}
		catch (IOException e)
		{
			logger.warn("Couldn't load logo", e);
		}
		Rectangle pageSize;
		switch (options.getPageSize())
		{
			case 1:
				pageSize = LEGAL;
				// imageSize = 567;
				break;
			default:
				pageSize = LETTER;
				break;
		}
		Document document = new Document(pageSize);
		document.addAuthor("Eric's Shipbuilder");
		document.setMargins(25, 25, 10, 10);

		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		try
		{

			PdfWriter w = getInstance(document, baos);

			w.setFullCompression();
			document.open();

			for (Ship ship : ships)
			{
				out.println("\t" + ship.getName() + " " + ship.getSpecies() + " " + ship.getSize());
				int imageSize = 567;

				switch (options.getPageSize())
				{
					default:
						if (options.isShowGuns())
						{
							imageSize -= 14;
						}
						if (options.isShowSpeed())
						{
							imageSize -= 14;
						}
						if (options.isShowHelm())
						{
							imageSize -= 14;
						}
						if (options.isShowShield())
						{
							imageSize -= 14;
						}
						if (options.isShowOCC())
						{
							imageSize -= 14;
						}
						if (options.isDamageTrack())
						{
							imageSize -= 32;
						}
						if (options.isShowNotes() && ship.getNotes().size() > 0)
						{
							imageSize -= 64;
						}
						break;
				}
				BufferedImage shipImage = ship.generateImage();

				java.awt.Image scaledShipImage = shipImage;
				float qualReduction = options.getOutputQualityReduction();
				if (qualReduction > 0.0)
				{
					int size = (int) (shipImage.getHeight() * (1.0 - qualReduction));
					size = max(size, imageSize); // don't scale smaller
					// than what we're going
					// to rescale it to
					// anyhow
					// System.out.println("Scaling ship image to " + size);
					scaledShipImage = getFasterScaledInstance(shipImage, size, size, VALUE_INTERPOLATION_BICUBIC,
															  true);
				}

				try
				{
					Paragraph p;

					PdfPTable table = new PdfPTable(4);
					table.setWidthPercentage(100);

					p = new Paragraph("Name: " + ship.getName(), getFont(HELVETICA, 10, NORMAL, BLACK));
					p.setAlignment(ALIGN_CENTER);
					PdfPCell cell = new PdfPCell(p);
					cell.setBorder(NO_BORDER);
					table.addCell(cell);

					p = new Paragraph("Registry: " + ship.getSpecies(),
									  getFont(HELVETICA, 10, NORMAL, BLACK));
					p.setAlignment(ALIGN_CENTER);
					cell = new PdfPCell(p);
					cell.setBorder(NO_BORDER);
					table.addCell(cell);

					p = new Paragraph("Size: " + ship.getSize(), getFont(HELVETICA, 10, NORMAL, BLACK));
					p.setAlignment(ALIGN_CENTER);
					cell = new PdfPCell(p);
					cell.setBorder(NO_BORDER);
					table.addCell(cell);

					if (logoImage != null)
					{
						com.lowagie.text.Image logoPdfImage = getInstance(logoImage, null);
						logoPdfImage.scalePercent(12f);
						cell = new PdfPCell(logoPdfImage);
						cell.setBorder(NO_BORDER);
						table.addCell(cell);
					}
					table.setSpacingAfter(2);
					document.add(table);

					if (options.isShowNotes() && ship.getNotes().size() > 0)
					{
						p = new Paragraph("Notes: " + ship.getNotesAsString(),
										  getFont(HELVETICA, 10, NORMAL, BLACK));
						document.add(p);
					}

					com.lowagie.text.Image shipPdfImage = getInstance(scaledShipImage, null);

					shipPdfImage.scaleAbsolute(imageSize, imageSize);
					shipPdfImage.setAlignment(MIDDLE);

					shipPdfImage.setSpacingAfter(2);
					document.add(shipPdfImage);

					document.add(new Paragraph(" "));

					if (options.isShowSpeed())
					{
						Color speedColor = CYAN.brighter().brighter();
						table = new PdfPTable(13);
						table.setWidthPercentage(100);
						p = new Paragraph("Speed", getFont(HELVETICA, 10, NORMAL,
														   speedColor));
						p.setAlignment(ALIGN_CENTER);
						cell = new PdfPCell(p);
						cell.setColspan(1);
						cell.setBorder(NO_BORDER);
						table.addCell(cell);

						for (int i = 0; i < 12; i++)
						{
							p = new Paragraph(Integer.toString(i), getFont(HELVETICA, 15, NORMAL, BLACK));
							p.setAlignment(ALIGN_CENTER);
							cell = new PdfPCell(p);
							// cell.setPadding(2);
							cell.setHorizontalAlignment(ALIGN_LEFT);
							cell.setBackgroundColor(speedColor);
							cell.setHorizontalAlignment(ALIGN_CENTER);
							cell.setBorder(BOX);
							table.addCell(cell);
						}
						if (options.isShowOCC())
						{
							table.setSpacingAfter(2);
						}
						else
						{
							table.setSpacingAfter(10);
						}
						document.add(table);
					}

					//
					// OOC
					//
					if (options.isShowOCC())
					{
						table = new PdfPTable(13);
						table.setWidthPercentage(100);
						p = new Paragraph("OOC", getFont(HELVETICA, 10, NORMAL, BLACK));
						p.setAlignment(ALIGN_CENTER);
						cell = new PdfPCell(p);
						cell.setColspan(2);
						cell.setBorder(NO_BORDER);
						table.addCell(cell);

						int maxOOC = 5;
						if (getInstance().getCurrentVersion() == Version1)
						{
							maxOOC = 7;
						}
						for (int i = 0; i < maxOOC; i++)
						{
							p = new Paragraph(Integer.toString(i), getFont(HELVETICA, 15, NORMAL, BLACK));
							p.setAlignment(ALIGN_CENTER);
							cell = new PdfPCell(p);
							cell.setHorizontalAlignment(ALIGN_LEFT);
							cell.setBackgroundColor(GRAY);
							cell.setHorizontalAlignment(ALIGN_CENTER);
							cell.setBorder(BOX);
							table.addCell(cell);
						}
						p = new Paragraph(" ", getFont(HELVETICA, 10, NORMAL, WHITE));
						p.setAlignment(ALIGN_CENTER);
						cell = new PdfPCell(p);
						cell.setColspan(11 - maxOOC);
						cell.setBorder(NO_BORDER);
						table.addCell(cell);

						table.setSpacingAfter(10);
						document.add(table);
					}

					//
					// Helm
					//
					if (options.isShowHelm())
					{
						table = new PdfPTable(13);
						table.setWidthPercentage(100);
						p = new Paragraph("Helm Power", getFont(HELVETICA, 10, NORMAL,
																Piloting.getColor()));
						p.setAlignment(ALIGN_CENTER);
						cell = new PdfPCell(p);
						cell.setColspan(2);
						cell.setBorder(NO_BORDER);
						table.addCell(cell);

						for (int i = 0; i < 11; i++)
						{
							if (i > ship.getDamageSize())
							{
								p = new Paragraph(" ", getFont(HELVETICA, 15, NORMAL, BLACK));
								p.setAlignment(ALIGN_CENTER);
								cell = new PdfPCell(p);
								cell
									.setHorizontalAlignment(ALIGN_CENTER);
								cell.setBorder(NO_BORDER);
							}
							else
							{
								p = new Paragraph(Integer.toString(i),
												  getFont(HELVETICA, 15, NORMAL, BLACK));
								p.setAlignment(ALIGN_CENTER);
								cell = new PdfPCell(p);
								cell.setHorizontalAlignment(ALIGN_LEFT);
								cell
									.setBackgroundColor(Piloting.getColor());
								cell
									.setHorizontalAlignment(ALIGN_CENTER);
								cell.setBorder(BOX);
							}

							table.addCell(cell);
						}
						table.setSpacingAfter(2);
						document.add(table);
					}

					if (options.isShowGuns())
					{
						table = new PdfPTable(13);
						table.setWidthPercentage(100);
						p = new Paragraph("Gun Power", getFont(HELVETICA, 10, NORMAL,
															   Combat.getColor()));
						p.setAlignment(ALIGN_CENTER);
						cell = new PdfPCell(p);
						cell.setColspan(2);
						cell.setBorder(NO_BORDER);
						table.addCell(cell);

						for (int i = 0; i < 11; i++)
						{
							if (i > ship.getDamageSize())
							{
								p = new Paragraph(" ", getFont(HELVETICA, 15, NORMAL, BLACK));
								p.setAlignment(ALIGN_CENTER);
								cell = new PdfPCell(p);
								cell
									.setHorizontalAlignment(ALIGN_CENTER);
								cell.setBorder(NO_BORDER);
							}
							else
							{
								p = new Paragraph(Integer.toString(i),
												  getFont(HELVETICA, 15, NORMAL, BLACK));
								p.setAlignment(ALIGN_CENTER);
								cell = new PdfPCell(p);
								cell.setHorizontalAlignment(ALIGN_LEFT);
								cell
									.setBackgroundColor(Combat.getColor());
								cell
									.setHorizontalAlignment(ALIGN_CENTER);
								cell.setBorder(BOX);
							}

							table.addCell(cell);
						}
						table.setSpacingAfter(2);
						document.add(table);
					}

					if (options.isShowShield())
					{
						table = new PdfPTable(13);
						table.setWidthPercentage(100);
						p = new Paragraph("Shield Power", getFont(HELVETICA, 10, NORMAL,
																  Science.getColor()));
						p.setAlignment(ALIGN_CENTER);
						cell = new PdfPCell(p);
						cell.setColspan(2);
						cell.setHorizontalAlignment(ALIGN_LEFT);
						cell.setBorder(NO_BORDER);
						table.addCell(cell);

						int shld = ship.getDamageSize();
						for (int i = 0; i < 11; i++)
						{
							if (options.isReverseShield())
							{
								if (shld > -1)
								{
									p = new Paragraph(Integer.toString(shld--),
													  getFont(HELVETICA, 15, NORMAL, BLACK));
									p.setAlignment(ALIGN_CENTER);
									cell = new PdfPCell(p);
									cell
										.setBackgroundColor(Science.getColor());
									cell
										.setHorizontalAlignment(ALIGN_CENTER);
									cell.setBorder(BOX);
								}
								else
								{
									p = new Paragraph(" ", getFont(HELVETICA, 15, NORMAL, BLACK));
									p.setAlignment(ALIGN_CENTER);
									cell = new PdfPCell(p);
									cell
										.setHorizontalAlignment(ALIGN_CENTER);
									cell.setBorder(NO_BORDER);
								}
							}
							else if (i > ship.getDamageSize())
							{
								p = new Paragraph(" ", getFont(HELVETICA, 15, NORMAL, BLACK));
								p.setAlignment(ALIGN_CENTER);
								cell = new PdfPCell(p);
								cell
									.setHorizontalAlignment(ALIGN_CENTER);
								cell.setBorder(NO_BORDER);
							}
							else
							{
								p = new Paragraph(Integer.toString(i),
												  getFont(HELVETICA, 15, NORMAL, BLACK));
								p.setAlignment(ALIGN_CENTER);
								cell = new PdfPCell(p);
								cell
									.setBackgroundColor(Science.getColor());
								cell
									.setHorizontalAlignment(ALIGN_CENTER);
								cell.setBorder(BOX);
							}
							table.addCell(cell);
						}

						table.setSpacingAfter(2);
						document.add(table);
					}

					int shipSize = ship.getDamageSize();
					if (options.isDamageTrack())
					{
						table = new PdfPTable(2);
						table.setWidthPercentage(100);
						p = new Paragraph("Damage Track", getFont(HELVETICA, 10, UNDERLINE, BLACK));
						p.setAlignment(ALIGN_CENTER);
						p.setSpacingAfter(2);
						document.add(p);

						p = new Paragraph(makeCircleString(shipSize * 4),
										  getFont(HELVETICA, 9, BOLD, BLACK));
						p.setAlignment(ALIGN_LEFT);
						// document.add(p);
						cell = new PdfPCell(p);
						cell.setBorder(NO_BORDER);
						cell.setColspan(2);
						table.addCell(cell);

						table.setWidthPercentage(100);

						for (int i = 3; i < 12; i++)
						{
							// damageIncrement += shipSize * 2;
							p = new Paragraph(makeCircleString(shipSize * 2)
												  + " (" + i + "+)", getFont(HELVETICA, 9, BOLD, BLACK));
							p.setAlignment(ALIGN_LEFT);
							// document.add(p);
							cell = new PdfPCell(p);
							cell.setBorder(NO_BORDER);
							table.addCell(cell);
						}

						// damageIncrement += shipSize * 2;
						p = new Paragraph(makeCircleString(shipSize * 2)
											  + " (12)", getFont(HELVETICA, 9, BOLD, BLACK));
						p.setAlignment(ALIGN_LEFT);
						// document.add(p);
						cell = new PdfPCell(p);
						cell.setBorder(NO_BORDER);
						table.addCell(cell);

						table.setSpacingAfter(2);
						document.add(table);

					}
					else if (options.isDamageChart())
					{
						table = new PdfPTable(17);
						table.setWidthPercentage(100);
						p = new Paragraph("Hull Damage", getFont(HELVETICA, 10, NORMAL, WHITE));
						p.setAlignment(ALIGN_CENTER);
						// document.add(p);
						cell = new PdfPCell(p);
						cell.setBackgroundColor(BLACK);
						cell.setHorizontalAlignment(ALIGN_CENTER);
						cell.setBorder(BOX);
						cell.setColspan(2);
						table.addCell(cell);

						p = new Paragraph("Size " + shipSize
											  + " Hull Rating Survival Chart", getFont(HELVETICA, 12, BOLD, BLACK));
						p.setAlignment(ALIGN_CENTER);
						cell = new PdfPCell(p);
						cell.setBackgroundColor(GRAY);
						cell.setBorder(BOX);
						cell.setHorizontalAlignment(ALIGN_CENTER);
						cell.setColspan(13);
						table.addCell(cell);

						p = new Paragraph("Chasing Missiles", getFont(HELVETICA, 10, NORMAL, WHITE));
						p.setAlignment(ALIGN_CENTER);
						cell = new PdfPCell(p);
						cell.setBackgroundColor(BLACK);
						cell.setHorizontalAlignment(ALIGN_CENTER);
						cell.setBorder(BOX);
						cell.setColspan(2);
						table.addCell(cell);

						p = new Paragraph(" ", getFont(HELVETICA, 13, NORMAL, BLACK));
						p.setAlignment(ALIGN_CENTER);
						// document.add(p);
						cell = new PdfPCell(p);
						cell.setBorder(LEFT | RIGHT);
						cell.setColspan(2);
						table.addCell(cell);

						p = new Paragraph("Hull Damage", getFont(HELVETICA, 10, NORMAL, WHITE));
						p.setAlignment(ALIGN_CENTER);
						cell = new PdfPCell(p);
						cell.setBackgroundColor(BLACK);
						cell.setHorizontalAlignment(ALIGN_CENTER);
						cell.setBorder(NO_BORDER);
						cell.setColspan(2);
						table.addCell(cell);

						int val = shipSize * 2;
						for (int i = 0; i < 10; i++)
						{
							val += (shipSize * 2);
							p = new Paragraph(Integer.toString(val),
											  getFont(HELVETICA,
													  10, NORMAL, WHITE));
							p.setAlignment(ALIGN_CENTER);
							cell = new PdfPCell(p);
							cell.setBackgroundColor(BLACK);
							cell.setHorizontalAlignment(ALIGN_CENTER);
							cell.setBorder(NO_BORDER);
							table.addCell(cell);
						}
						val += (shipSize * 2);
						p = new Paragraph(val + "+", getFont(HELVETICA, 10, NORMAL, WHITE));
						p.setAlignment(ALIGN_CENTER);
						cell = new PdfPCell(p);
						cell.setBackgroundColor(BLACK);
						cell.setHorizontalAlignment(ALIGN_CENTER);
						cell.setBorder(NO_BORDER);
						table.addCell(cell);

						p = new Paragraph(" ", getFont(HELVETICA, 13, NORMAL, BLACK));
						p.setAlignment(ALIGN_CENTER);
						// document.add(p);
						cell = new PdfPCell(p);
						cell.setBorder(LEFT | RIGHT);
						cell.setColspan(2);
						table.addCell(cell);

						p = new Paragraph(" ", getFont(HELVETICA, 13, NORMAL, BLACK));
						p.setAlignment(ALIGN_CENTER);
						// document.add(p);
						cell = new PdfPCell(p);
						cell.setBorder(LEFT | RIGHT
										   | BOTTOM);
						cell.setColspan(2);
						table.addCell(cell);

						p = new Paragraph("Hull Check (2D6):", getFont(HELVETICA, 6, NORMAL, BLACK));
						p.setAlignment(ALIGN_CENTER);
						cell = new PdfPCell(p);
						cell.setHorizontalAlignment(ALIGN_CENTER);
						cell.setBorder(BOX);
						cell.setColspan(2);
						table.addCell(cell);

						for (int i = 3; i < 12; i++)
						{
							p = new Paragraph(i + "+", getFont(HELVETICA, 10, NORMAL, BLACK));
							p.setAlignment(ALIGN_CENTER);
							cell = new PdfPCell(p);
							cell.setHorizontalAlignment(ALIGN_CENTER);
							cell.setBorder(BOX);
							table.addCell(cell);
						}

						p = new Paragraph("12", getFont(HELVETICA, 10, NORMAL, BLACK));
						p.setAlignment(ALIGN_CENTER);
						cell = new PdfPCell(p);
						cell.setHorizontalAlignment(ALIGN_CENTER);
						cell.setBorder(BOX);
						table.addCell(cell);

						p = new Paragraph("X", getFont(HELVETICA, 10, BOLD, BLACK));
						p.setAlignment(ALIGN_CENTER);
						cell = new PdfPCell(p);
						cell.setHorizontalAlignment(ALIGN_CENTER);
						cell.setBorder(BOX);
						table.addCell(cell);

						p = new Paragraph(" ", getFont(HELVETICA, 13, NORMAL, BLACK));
						p.setAlignment(ALIGN_CENTER);
						// document.add(p);
						cell = new PdfPCell(p);
						cell.setBorder(LEFT | RIGHT
										   | BOTTOM);
						cell.setColspan(2);
						table.addCell(cell);

						table.setSpacingAfter(2);
						document.add(table);
					}

					p = new Paragraph("www.battlestations.info", getFont(HELVETICA, 7, BOLDITALIC, BLUE));
					/*
                     p = new Paragraph("Created with Eric's Ship Creator on "
                     + new Date().toString(), FontFactory.getFont(
                     FontFactory.HELVETICA, 7, Font.BOLDITALIC,
                     Color.MAGENTA));
					 */
					p.setAlignment(ALIGN_CENTER);
					document.add(p);

					document.newPage();

				}
				catch (DocumentException e)
				{
					logger.warn("Error creating pdf", e);
				}
			}
			document.close();
			// Prepare to write to the file
			baos.writeTo(outStream);
			outStream.close();
		}
		catch (DocumentException e1)
		{
			logger.warn("Error writing pdf", e1);
		}

	}

	public static void drawPDF(Ship ship, OutputStream outStream,
							   PDFShipWriterOptions options) throws IOException
	{
		BufferedImage logoImage = null;
		InputStream imageStream = Ship.class
			.getResourceAsStream("/com/ericski/Battlestations/Images/batstationlogo.gif");
		try
		{
			logoImage = read(imageStream);
		}
		catch (IOException e)
		{
			logger.warn("Couldn't load logo", e);
		}

		BufferedImage shipImage = ship.generateImage();

		int imageSize = 567;
		Rectangle pageSize;
		switch (options.getPageSize())
		{
			case 1:
				pageSize = LEGAL;
				// imageSize = 567;
				break;
			default:
				pageSize = LETTER;
				if (options.isShowGuns())
				{
					imageSize -= 14;
				}
				if (options.isShowSpeed())
				{
					imageSize -= 14;
				}
				if (options.isShowHelm())
				{
					imageSize -= 14;
				}
				if (options.isShowShield())
				{
					imageSize -= 14;
				}
				if (options.isShowOCC())
				{
					imageSize -= 14;
				}
				if (options.isDamageTrack())
				{
					imageSize -= 32;
				}
				if (options.isShowNotes() && ship.getNotes().size() > 0)
				{
					imageSize -= 64;
				}
				break;
		}

		java.awt.Image scaledShipImage = shipImage;
		float qualReduction = options.getOutputQualityReduction();
		if (qualReduction > 0.0)
		{
			int size = (int) (shipImage.getHeight() * (1.0 - qualReduction));
			size = max(size, imageSize); // don't scale smaller than what
			// we're going to rescale it to
			// anyhow
			// System.out.println("Scaling ship image to " + size);
			scaledShipImage = getFasterScaledInstance(shipImage, size, size, VALUE_INTERPOLATION_BICUBIC, true);
		}

		Document document = new Document(pageSize);
		document.addAuthor("Eric's Shipbuilder");
		document.setMargins(25, 25, 10, 10);

		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		try
		{
			PdfWriter w = getInstance(document, baos);
			w.setFullCompression();
			document.open();
			Paragraph p;

			PdfPTable table = new PdfPTable(4);
			table.setWidthPercentage(100);

			p = new Paragraph("Name: " + ship.getName(), getFont(HELVETICA, 10, NORMAL, BLACK));
			p.setAlignment(ALIGN_CENTER);
			PdfPCell cell = new PdfPCell(p);
			cell.setBorder(NO_BORDER);
			table.addCell(cell);

			p = new Paragraph("Registry: " + ship.getSpecies(), getFont(HELVETICA, 10, NORMAL, BLACK));
			p.setAlignment(ALIGN_CENTER);
			cell = new PdfPCell(p);
			cell.setBorder(NO_BORDER);
			table.addCell(cell);

			p = new Paragraph("Size: " + ship.getSize(), getFont(HELVETICA, 10, NORMAL, BLACK));
			p.setAlignment(ALIGN_CENTER);
			cell = new PdfPCell(p);
			cell.setBorder(NO_BORDER);
			table.addCell(cell);

			if (logoImage != null)
			{
				com.lowagie.text.Image logoPdfImage = getInstance(logoImage, null);
				logoPdfImage.scalePercent(12f);
				cell = new PdfPCell(logoPdfImage);
				cell.setBorder(NO_BORDER);
				table.addCell(cell);
			}
			table.setSpacingAfter(2);
			document.add(table);

			if (options.isShowNotes() && ship.getNotes().size() > 0)
			{
				p = new Paragraph("Notes: " + ship.getNotesAsString(),
								  getFont(HELVETICA, 10, NORMAL, BLACK));
				document.add(p);
			}

			com.lowagie.text.Image shipPdfImage = getInstance(scaledShipImage, null);

			shipPdfImage.scaleAbsolute(imageSize, imageSize);
			shipPdfImage.setAlignment(MIDDLE);

			shipPdfImage.setSpacingAfter(2);
			document.add(shipPdfImage);

			document.add(new Paragraph(" "));

			if (options.isShowSpeed())
			{
				Color speedColor = CYAN.brighter().brighter();
				table = new PdfPTable(13);
				table.setWidthPercentage(100);
				p = new Paragraph("Speed", getFont(HELVETICA, 10, NORMAL, speedColor));
				p.setAlignment(ALIGN_CENTER);
				cell = new PdfPCell(p);
				cell.setColspan(1);
				cell.setBorder(NO_BORDER);
				table.addCell(cell);

				for (int i = 0; i < 12; i++)
				{
					p = new Paragraph(Integer.toString(i), getFont(HELVETICA, 15, NORMAL, BLACK));
					p.setAlignment(ALIGN_CENTER);
					cell = new PdfPCell(p);
					// cell.setPadding(2);
					cell.setHorizontalAlignment(ALIGN_LEFT);
					cell.setBackgroundColor(speedColor);
					cell.setHorizontalAlignment(ALIGN_CENTER);
					cell.setBorder(BOX);
					table.addCell(cell);
				}
				if (options.isShowOCC())
				{
					table.setSpacingAfter(2);
				}
				else
				{
					table.setSpacingAfter(10);
				}
				document.add(table);
			}

			//
			// OOC
			//
			if (options.isShowOCC())
			{
				table = new PdfPTable(13);
				table.setWidthPercentage(100);
				p = new Paragraph("OOC", getFont(HELVETICA, 10, NORMAL, BLACK));
				p.setAlignment(ALIGN_CENTER);
				cell = new PdfPCell(p);
				cell.setColspan(2);
				cell.setBorder(NO_BORDER);
				table.addCell(cell);

				int maxOOC = 5;
				if (getInstance().getCurrentVersion() == Version1)
				{
					maxOOC = 7;
				}
				for (int i = 0; i < maxOOC; i++)
				{
					p = new Paragraph(Integer.toString(i), getFont(HELVETICA, 15, NORMAL, BLACK));
					p.setAlignment(ALIGN_CENTER);
					cell = new PdfPCell(p);
					cell.setHorizontalAlignment(ALIGN_LEFT);
					cell.setBackgroundColor(GRAY);
					cell.setHorizontalAlignment(ALIGN_CENTER);
					cell.setBorder(BOX);
					table.addCell(cell);
				}
				p = new Paragraph(" ", getFont(HELVETICA, 10, NORMAL, WHITE));
				p.setAlignment(ALIGN_CENTER);
				cell = new PdfPCell(p);
				cell.setColspan(11 - maxOOC);
				cell.setBorder(NO_BORDER);
				table.addCell(cell);

				table.setSpacingAfter(10);
				document.add(table);
			}

			//
			// Helm
			//
			if (options.isShowHelm())
			{
				table = new PdfPTable(13);
				table.setWidthPercentage(100);
				p = new Paragraph("Helm Power", getFont(HELVETICA, 10, NORMAL,
														Piloting.getColor()));
				p.setAlignment(ALIGN_CENTER);
				cell = new PdfPCell(p);
				cell.setColspan(2);
				cell.setBorder(NO_BORDER);
				table.addCell(cell);

				for (int i = 0; i < 11; i++)
				{
					if (i > ship.getDamageSize())
					{
						p = new Paragraph(" ", getFont(HELVETICA, 15, NORMAL, BLACK));
						p.setAlignment(ALIGN_CENTER);
						cell = new PdfPCell(p);
						cell.setHorizontalAlignment(ALIGN_CENTER);
						cell.setBorder(NO_BORDER);
					}
					else
					{
						p = new Paragraph(Integer.toString(i), getFont(HELVETICA, 15, NORMAL, BLACK));
						p.setAlignment(ALIGN_CENTER);
						cell = new PdfPCell(p);
						cell.setHorizontalAlignment(ALIGN_LEFT);
						cell.setBackgroundColor(Piloting.getColor());
						cell.setHorizontalAlignment(ALIGN_CENTER);
						cell.setBorder(BOX);
					}

					table.addCell(cell);
				}
				table.setSpacingAfter(2);
				document.add(table);
			}

			if (options.isShowGuns())
			{
				table = new PdfPTable(13);
				table.setWidthPercentage(100);
				p = new Paragraph("Gun Power", getFont(HELVETICA, 10, NORMAL,
													   Combat.getColor()));
				p.setAlignment(ALIGN_CENTER);
				cell = new PdfPCell(p);
				cell.setColspan(2);
				cell.setBorder(NO_BORDER);
				table.addCell(cell);

				for (int i = 0; i < 11; i++)
				{
					if (i > ship.getDamageSize())
					{
						p = new Paragraph(" ", getFont(HELVETICA, 15, NORMAL, BLACK));
						p.setAlignment(ALIGN_CENTER);
						cell = new PdfPCell(p);
						cell.setHorizontalAlignment(ALIGN_CENTER);
						cell.setBorder(NO_BORDER);
					}
					else
					{
						p = new Paragraph(Integer.toString(i), getFont(HELVETICA, 15, NORMAL, BLACK));
						p.setAlignment(ALIGN_CENTER);
						cell = new PdfPCell(p);
						cell.setHorizontalAlignment(ALIGN_LEFT);
						cell.setBackgroundColor(Combat.getColor());
						cell.setHorizontalAlignment(ALIGN_CENTER);
						cell.setBorder(BOX);
					}

					table.addCell(cell);
				}
				table.setSpacingAfter(2);
				document.add(table);
			}

			if (options.isShowShield())
			{
				table = new PdfPTable(13);
				table.setWidthPercentage(100);
				p = new Paragraph("Shield Power", getFont(HELVETICA, 10, NORMAL,
														  Science.getColor()));
				p.setAlignment(ALIGN_CENTER);
				cell = new PdfPCell(p);
				cell.setColspan(2);
				cell.setHorizontalAlignment(ALIGN_LEFT);
				cell.setBorder(NO_BORDER);
				table.addCell(cell);

				int shld = ship.getDamageSize();
				for (int i = 0; i < 11; i++)
				{
					if (options.isReverseShield())
					{
						if (shld > -1)
						{
							p = new Paragraph(Integer.toString(shld--),
											  getFont(HELVETICA,
													  15, NORMAL, BLACK));
							p.setAlignment(ALIGN_CENTER);
							cell = new PdfPCell(p);
							cell
								.setBackgroundColor(Science.getColor());
							cell.setHorizontalAlignment(ALIGN_CENTER);
							cell.setBorder(BOX);
						}
						else
						{
							p = new Paragraph(" ", getFont(HELVETICA, 15, NORMAL, BLACK));
							p.setAlignment(ALIGN_CENTER);
							cell = new PdfPCell(p);
							cell.setHorizontalAlignment(ALIGN_CENTER);
							cell.setBorder(NO_BORDER);
						}
					}
					else if (i > ship.getDamageSize())
					{
						p = new Paragraph(" ", getFont(HELVETICA, 15, NORMAL, BLACK));
						p.setAlignment(ALIGN_CENTER);
						cell = new PdfPCell(p);
						cell.setHorizontalAlignment(ALIGN_CENTER);
						cell.setBorder(NO_BORDER);
					}
					else
					{
						p = new Paragraph(Integer.toString(i), getFont(HELVETICA, 15, NORMAL, BLACK));
						p.setAlignment(ALIGN_CENTER);
						cell = new PdfPCell(p);
						cell
							.setBackgroundColor(Science.getColor());
						cell.setHorizontalAlignment(ALIGN_CENTER);
						cell.setBorder(BOX);
					}
					table.addCell(cell);
				}

				table.setSpacingAfter(2);
				document.add(table);
			}

			int shipSize = ship.getDamageSize();
			if (options.isDamageTrack())
			{
				table = new PdfPTable(2);
				table.setWidthPercentage(100);
				p = new Paragraph("Damage Track", getFont(HELVETICA, 10, UNDERLINE, BLACK));
				p.setAlignment(ALIGN_CENTER);
				p.setSpacingAfter(2);
				document.add(p);

				p = new Paragraph(makeCircleString(shipSize * 4), getFont(HELVETICA, 9, BOLD, BLACK));
				p.setAlignment(ALIGN_LEFT);
				// document.add(p);
				cell = new PdfPCell(p);
				cell.setBorder(NO_BORDER);
				cell.setColspan(2);
				table.addCell(cell);

				table.setWidthPercentage(100);

				for (int i = 3; i < 12; i++)
				{
					// damageIncrement += shipSize * 2;
					p = new Paragraph(makeCircleString(shipSize * 2) + " (" + i
										  + "+)", getFont(HELVETICA,
														  9, BOLD, BLACK));
					p.setAlignment(ALIGN_LEFT);
					// document.add(p);
					cell = new PdfPCell(p);
					cell.setBorder(NO_BORDER);
					table.addCell(cell);
				}

				// damageIncrement += shipSize * 2;
				p = new Paragraph(makeCircleString(shipSize * 2) + " (12)",
								  getFont(HELVETICA, 9, BOLD, BLACK));
				p.setAlignment(ALIGN_LEFT);
				// document.add(p);
				cell = new PdfPCell(p);
				cell.setBorder(NO_BORDER);
				table.addCell(cell);

				table.setSpacingAfter(2);
				document.add(table);

			}
			else if (options.isDamageChart())
			{
				table = new PdfPTable(17);
				table.setWidthPercentage(100);
				p = new Paragraph("Hull Damage", getFont(HELVETICA, 10, NORMAL, WHITE));
				p.setAlignment(ALIGN_CENTER);
				// document.add(p);
				cell = new PdfPCell(p);
				cell.setBackgroundColor(BLACK);
				cell.setHorizontalAlignment(ALIGN_CENTER);
				cell.setBorder(BOX);
				cell.setColspan(2);
				table.addCell(cell);

				p = new Paragraph("Size " + shipSize
									  + " Hull Rating Survival Chart", getFont(HELVETICA, 12, BOLD, BLACK));
				p.setAlignment(ALIGN_CENTER);
				cell = new PdfPCell(p);
				cell.setBackgroundColor(GRAY);
				cell.setBorder(BOX);
				cell.setHorizontalAlignment(ALIGN_CENTER);
				cell.setColspan(13);
				table.addCell(cell);

				p = new Paragraph("Chasing Missiles", getFont(HELVETICA, 10, NORMAL, WHITE));
				p.setAlignment(ALIGN_CENTER);
				cell = new PdfPCell(p);
				cell.setBackgroundColor(BLACK);
				cell.setHorizontalAlignment(ALIGN_CENTER);
				cell.setBorder(BOX);
				cell.setColspan(2);
				table.addCell(cell);

				p = new Paragraph(" ", getFont(HELVETICA, 13, NORMAL, BLACK));
				p.setAlignment(ALIGN_CENTER);
				// document.add(p);
				cell = new PdfPCell(p);
				cell.setBorder(LEFT | RIGHT);
				cell.setColspan(2);
				table.addCell(cell);

				p = new Paragraph("Hull Damage", getFont(HELVETICA, 10, NORMAL, WHITE));
				p.setAlignment(ALIGN_CENTER);
				cell = new PdfPCell(p);
				cell.setBackgroundColor(BLACK);
				cell.setHorizontalAlignment(ALIGN_CENTER);
				cell.setBorder(NO_BORDER);
				cell.setColspan(2);
				table.addCell(cell);

				int val = shipSize * 2;
				for (int i = 0; i < 10; i++)
				{
					val += (shipSize * 2);
					p = new Paragraph(Integer.toString(val), getFont(HELVETICA, 10, NORMAL, WHITE));
					p.setAlignment(ALIGN_CENTER);
					cell = new PdfPCell(p);
					cell.setBackgroundColor(BLACK);
					cell.setHorizontalAlignment(ALIGN_CENTER);
					cell.setBorder(NO_BORDER);
					table.addCell(cell);
				}
				val += (shipSize * 2);
				p = new Paragraph(val + "+", getFont(HELVETICA, 10, NORMAL, WHITE));
				p.setAlignment(ALIGN_CENTER);
				cell = new PdfPCell(p);
				cell.setBackgroundColor(BLACK);
				cell.setHorizontalAlignment(ALIGN_CENTER);
				cell.setBorder(NO_BORDER);
				table.addCell(cell);

				p = new Paragraph(" ", getFont(HELVETICA, 13, NORMAL, BLACK));
				p.setAlignment(ALIGN_CENTER);
				// document.add(p);
				cell = new PdfPCell(p);
				cell.setBorder(LEFT | RIGHT);
				cell.setColspan(2);
				table.addCell(cell);

				p = new Paragraph(" ", getFont(HELVETICA, 13, NORMAL, BLACK));
				p.setAlignment(ALIGN_CENTER);
				// document.add(p);
				cell = new PdfPCell(p);
				cell.setBorder(LEFT | RIGHT
								   | BOTTOM);
				cell.setColspan(2);
				table.addCell(cell);

				p = new Paragraph("Hull Check (2D6):", getFont(HELVETICA, 6, NORMAL, BLACK));
				p.setAlignment(ALIGN_CENTER);
				cell = new PdfPCell(p);
				cell.setHorizontalAlignment(ALIGN_CENTER);
				cell.setBorder(BOX);
				cell.setColspan(2);
				table.addCell(cell);

				for (int i = 3; i < 12; i++)
				{
					p = new Paragraph(i + "+", getFont(HELVETICA, 10, NORMAL, BLACK));
					p.setAlignment(ALIGN_CENTER);
					cell = new PdfPCell(p);
					cell.setHorizontalAlignment(ALIGN_CENTER);
					cell.setBorder(BOX);
					table.addCell(cell);
				}

				p = new Paragraph("12", getFont(HELVETICA, 10, NORMAL, BLACK));
				p.setAlignment(ALIGN_CENTER);
				cell = new PdfPCell(p);
				cell.setHorizontalAlignment(ALIGN_CENTER);
				cell.setBorder(BOX);
				table.addCell(cell);

				p = new Paragraph("X", getFont(HELVETICA, 10, BOLD, BLACK));
				p.setAlignment(ALIGN_CENTER);
				cell = new PdfPCell(p);
				cell.setHorizontalAlignment(ALIGN_CENTER);
				cell.setBorder(BOX);
				table.addCell(cell);

				p = new Paragraph(" ", getFont(HELVETICA, 13, NORMAL, BLACK));
				p.setAlignment(ALIGN_CENTER);
				// document.add(p);
				cell = new PdfPCell(p);
				cell.setBorder(LEFT | RIGHT
								   | BOTTOM);
				cell.setColspan(2);
				table.addCell(cell);

				table.setSpacingAfter(2);
				document.add(table);
			}

			p = new Paragraph("Created with Eric's Ship Creator on "
								  + new Date().toString(), getFont(HELVETICA, 7, BOLDITALIC, MAGENTA));
			p.setAlignment(ALIGN_CENTER);
			document.add(p);

			document.close();
		}
		catch (DocumentException e)
		{
			logger.warn("Error creating pdf", e);
		}

		// Prepare to write to the file
		baos.writeTo(outStream);
		outStream.close();

	}

	private static String makeCircleString(int number)
	{
		StringBuilder buf = new StringBuilder(number);
		for (int i = 0; i < number; i++)
		{
			if (i % 5 == 0)
			{
				buf.append(' ');
			}
			buf.append('O');

		}
		return buf.toString();
	}

	public static void main(String[] args) throws Exception
	{
		out.println("Starting");
		List<Ship> ships = INSTANCE.getStandardTemplates();
		PDFShipWriterOptions options = new PDFShipWriterOptions();
		//options.setOutputQualityReduction(.6F);
		options.setShowHelm(false);
		options.setShowGuns(true);
		options.setShowOCC(false);
		options.setShowSpeed(true);
		options.setShowShield(true);
		options.setReverseShield(true);
		options.setDamageChart(true);

		//System.out.println("Removing Generic Ships");
		List<Ship> newList = new ArrayList<>();
		for (Ship ship : ships)
		{
			if (ship.getSpecies() != null && ship.getSpecies().length() > 0
					&& !ship.getSpecies().equalsIgnoreCase("Dr Moreau") && !ship.getSpecies().equalsIgnoreCase("Doids")
					&& !ship.getSpecies().equalsIgnoreCase("Eugene"))
			{
				newList.add(ship);
			}
		}
		out.println("Creating File");
		OutputStream outStream = new FileOutputStream("N:\\FAShips_high.pdf");
		drawPDF(newList, outStream, options);
		out.println("Finished");

	}
}
