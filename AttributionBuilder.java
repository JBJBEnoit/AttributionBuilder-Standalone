/**!
Copyright (c) 2023 Jason Benoit and David Giesbrecht

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in
all copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
THE SOFTWARE.

**This text is from: http://opensource.org/licenses/MIT**
!**/

import java.io.*;
import java.awt.datatransfer.StringSelection;
import java.awt.Toolkit;
import javax.swing.*;
import javax.swing.text.JTextComponent;

import java.awt.*;
import java.awt.event.*;
import org.jsoup.*;
import org.jsoup.select.*;

import org.jsoup.nodes.*;
public class AttributionBuilder extends JFrame
{

	private static final long serialVersionUID = 1L;
	
	enum Resource {PRESSBOOKS, OPEN_STAX, OTHER};
	
	JTextField source;
	JLabel sourceLbl, attrLabel, copyStatusLbl;
	JTextArea attributionTxt;
	JButton buildBtn, copyBtn;
	JLabel pageTitleLabel, bookTitleLabel, bookUrlLabel, authorLabel, licenseTypeLabel, licenseUrlLabel;
	JTextField bookTitleField, bookURLField, pageTitleField, authorField, licenseTypeField, licenseURLField;
	JPanel bookTitleLblPanel, bookTitlePanel, pageTitleLblPanel, pageTitlePanel, bookUrlLblPanel, bookUrlPanel, authorLblPanel,
		   authorPanel, licenseTypeLblPanel, licenseTypePanel, licenseUrlLblPanel, licenseUrlPanel;
	boolean manualInputFieldsVisible = false;

	
	public AttributionBuilder()
	{
		super("Attribution Builder");
		
		this.setDefaultCloseOperation(EXIT_ON_CLOSE);//destroy this object when close button is pressed
		this.setSize(400, 415); //width and height in pixels
		this.setLocationRelativeTo(null);//centers the JFrame on the screen.
		Image icon = new ImageIcon(getClass().getResource("images/attribution_builder_icon.png")).getImage();
		this.setIconImage(icon);
		this.setLayout(new FlowLayout());
		
		
		JPanel mainPanel = new JPanel();
		mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
		
		
		Listener listener = new Listener(this);
		
		JPanel inputPanel = new JPanel();
		inputPanel.setLayout(new BoxLayout(inputPanel,BoxLayout.Y_AXIS));
		sourceLbl = new JLabel("Source URL:");
		JPanel srcLblPanel = new JPanel();
		srcLblPanel.add(sourceLbl);
		source = new JTextField();
		source.setColumns(30);
		
		// Select all the contents of the source input field when clicked
		// for easy replacement via copy-paste
		source.addFocusListener(new FocusListener() {
			@Override
			public void focusGained(FocusEvent e)
			{
				((JTextComponent) e.getSource()).selectAll();			
			}
			@Override
			public void focusLost(FocusEvent e){}			
		});
		JPanel sourcePanel = new JPanel();
		sourcePanel.add(source);
		buildBtn = new JButton("Build Attribution");
		JPanel bBtnPanel = new JPanel();
		
		buildBtn.setPreferredSize(new Dimension(160, 40));
		buildBtn.addActionListener(listener);
		bBtnPanel.add(buildBtn);
		
		pageTitleLabel = new JLabel("Page Title:");
		pageTitleLblPanel = new JPanel();
		pageTitleLblPanel.add(pageTitleLabel);
		pageTitleLblPanel.setVisible(false);
		
		pageTitleField = new JTextField();
		pageTitleField.setColumns(30);
		pageTitlePanel = new JPanel();
		pageTitlePanel.add(pageTitleField);
		pageTitlePanel.setVisible(false);
		
		bookTitleLabel = new JLabel("Book Title:");
		bookTitleLblPanel = new JPanel();
		bookTitleLblPanel.add(bookTitleLabel);
		bookTitleLblPanel.setVisible(false);
		
		bookTitleField = new JTextField();
		bookTitleField.setColumns(30);
		bookTitlePanel = new JPanel();
		bookTitlePanel.add(bookTitleField);
		bookTitlePanel.setVisible(false);
		
		bookUrlLabel = new JLabel("Book URL:");
		bookUrlLblPanel = new JPanel();
		bookUrlLblPanel.add(bookUrlLabel);
		bookUrlLblPanel.setVisible(false);
		
		bookURLField = new JTextField();
		bookURLField.setColumns(30);
		bookUrlPanel = new JPanel();
		bookUrlPanel.add(bookURLField);
		bookUrlPanel.setVisible(false);
		
		authorLabel = new JLabel("Author:");
		authorLblPanel = new JPanel();
		authorLblPanel.add(authorLabel);
		authorLblPanel.setVisible(false);
		
		authorField = new JTextField();
		authorField.setColumns(30);
		authorPanel = new JPanel();
		authorPanel.add(authorField);
		authorPanel.setVisible(false);
		
		licenseTypeLabel = new JLabel("License Type:");
		licenseTypeLblPanel = new JPanel();
		licenseTypeLblPanel.add(licenseTypeLabel);
		licenseTypeLblPanel.setVisible(false);
		
		licenseTypeField = new JTextField();
		licenseTypeField.setColumns(30);
		licenseTypePanel = new JPanel();
		licenseTypePanel.add(licenseTypeField);
		licenseTypePanel.setVisible(false);
		
		licenseUrlLabel = new JLabel("License URL:");
		licenseUrlLblPanel = new JPanel();
		licenseUrlLblPanel.add(licenseUrlLabel);
		licenseUrlLblPanel.setVisible(false);
		
		licenseURLField = new JTextField();
		licenseURLField.setColumns(30);
		licenseUrlPanel = new JPanel();
		licenseUrlPanel.add(licenseURLField);
		licenseUrlPanel.setVisible(false);
		
		inputPanel.add(srcLblPanel);
		inputPanel.add(sourcePanel);
		
		inputPanel.add(pageTitleLblPanel);
		inputPanel.add(pageTitlePanel);
		inputPanel.add(bookTitleLblPanel);
		inputPanel.add(bookTitlePanel);
		inputPanel.add(bookUrlLblPanel);
		inputPanel.add(bookUrlPanel);
		inputPanel.add(authorLblPanel);
		inputPanel.add(authorPanel);
		inputPanel.add(licenseTypeLblPanel);
		inputPanel.add(licenseTypePanel);
		inputPanel.add(licenseUrlLblPanel);
		inputPanel.add(licenseUrlPanel);
		
		inputPanel.add(bBtnPanel);

		JPanel outputPanel = new JPanel();
		outputPanel.setLayout(new BoxLayout(outputPanel,BoxLayout.Y_AXIS));
		
		
		attrLabel = new JLabel("Attribution");
		JPanel attrLblPanel = new JPanel();
		attrLblPanel.add(attrLabel);
		attributionTxt = new JTextArea(4, 30);
		attributionTxt.setLineWrap(true);
		attributionTxt.setEditable(false);
		JScrollPane scroll = new JScrollPane(attributionTxt);
		scroll.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED);

		JPanel copyBtnPanel = new JPanel();
		JPanel copyPanel = new JPanel();
		copyPanel.setLayout(new GridLayout(2, 1));
		copyBtn = new JButton("Copy to clipboard");
		copyBtn.addActionListener(listener);
		copyBtn.setPreferredSize(new Dimension(160, 40));
		
		copyStatusLbl = new JLabel("");
		JPanel copyStatusPanel = new JPanel();
		copyStatusPanel.add(copyStatusLbl);
		copyBtnPanel.add(copyBtn);
		copyPanel.add(copyBtnPanel);
		copyPanel.add(copyStatusPanel);
		outputPanel.add(attrLblPanel);
		outputPanel.add(scroll);
		outputPanel.add(copyPanel);
		
		mainPanel.add(inputPanel);
		mainPanel.add(outputPanel);
		this.add(mainPanel);
		
		this.setVisible(true);
	}

	
	private class Attribution
	{
		public String pageURL;
    public String pageTitle;
    public String bookURL;
    public String bookTitle;
    public String author;
    public String licenseURL;
    public String licenseType;
    
    public String getAttribution()
    {
    	String output = "\"<a href='" + pageURL + "'>" + pageTitle;
    	output += "</a>\" from <a href='" + bookURL +"'>" + bookTitle + "</a>";
    	output += " by " + author + " is licensed under a <a href='" + licenseURL;
    	output += "'>" + licenseType + "</a>, except where otherwise noted.";
    	
    	return output;
    }
	}
	
	private class Listener implements ActionListener
	{
		AttributionBuilder frame;
		
		public Listener(AttributionBuilder frame)
		{
			this.frame = frame;
		}

		@Override
		public void actionPerformed(ActionEvent ev)
		{
			if (ev.getSource() == buildBtn)
			{
				copyStatusLbl.setText("");
				String url = source.getText();
							
				try
				{
					Document doc = Jsoup.connect(url).get();
					Attribution attribution = new Attribution();
					attribution.pageURL = url;
					Resource type = setType(doc, url);
					
					if (type == Resource.PRESSBOOKS || isPressbooks(doc))
					{
						if (manualInputFieldsVisible)
						{
							manualInputFieldsVisible = toggleVisibility();
						}
						
						buildPressbooksAttribution(doc, attribution);
					}
					else if (type == Resource.OPEN_STAX)
					{
						if (manualInputFieldsVisible)
						{
							manualInputFieldsVisible = toggleVisibility();
						}
						
						buildOpenStaxAttribution(doc, attribution);
					}
					else
					{
						if (!manualInputFieldsVisible)
						{
							manualInputFieldsVisible = toggleVisibility();
						}
						
						buildManualAttribution(attribution);
					}					
				}
				catch(IOException ex)
				{
					if (!manualInputFieldsVisible)
					{
						manualInputFieldsVisible = toggleVisibility();
					}
					Attribution attribution = new Attribution();
					attribution.pageURL = url;

					buildManualAttribution(attribution);
					ex.printStackTrace();
				}			
			}
			else if (ev.getSource() == copyBtn)
			{
				Toolkit.getDefaultToolkit().getSystemClipboard()
					.setContents(new StringSelection(attributionTxt.getText()), null);
				copyStatusLbl.setText("Copied!");				
			}		
		}
		
		private Resource setType(Document doc, String url)
		{
			Resource type = Resource.OTHER;
			
			if (url.contains("openstax"))
			{
				type = Resource.OPEN_STAX;
			}
			else if (url.contains("pressbooks") || isPressbooks(doc))
			{
				type = Resource.PRESSBOOKS;
			}
			
			return type;
		}
		
		private boolean isPressbooks(Document doc)
		{
			Elements elements = doc.getAllElements();
			
			for (Element el : elements)
			{
				if (el.className().contains("pressbooks"))
				{
					return true;
				}
			}
			
			return false;		
		}
		
		private boolean toggleVisibility()
		{
			pageTitleLblPanel.setVisible(!pageTitleLblPanel.isVisible());
			pageTitlePanel.setVisible(!pageTitlePanel.isVisible());
			bookTitleLblPanel.setVisible(!bookTitleLblPanel.isVisible());
			bookTitlePanel.setVisible(!bookTitlePanel.isVisible());
			bookUrlLblPanel.setVisible(!bookUrlLblPanel.isVisible());
			bookUrlPanel.setVisible(!bookUrlPanel.isVisible());
			authorLblPanel.setVisible(!authorLblPanel.isVisible());
			authorPanel.setVisible(!authorPanel.isVisible());
			licenseTypeLblPanel.setVisible(!licenseTypeLblPanel.isVisible());
			licenseTypePanel.setVisible(!licenseTypePanel.isVisible());
			licenseUrlLblPanel.setVisible(!licenseUrlLblPanel.isVisible());
			licenseUrlPanel.setVisible(!licenseUrlPanel.isVisible());
			
			if (pageTitleLblPanel.isVisible())
			{
				frame.setSize(400, 654); //width and height in pixels
				
			}
			else
			{
				frame.setSize(400, 400);
				
			}
						
			return pageTitleLblPanel.isVisible();
		}

		
		private void buildPressbooksAttribution(Document doc, Attribution attribution)
		{
			Elements anchorTags = doc.getElementsByTag("a");

			
			for (Element el : anchorTags)
			{
				if (el.attr("rel").equals("cc:attributionURL"))
				{
					attribution.bookURL = el.attr("href");
					attribution.bookTitle = el.html();
				}
				else if (el.attr("rel").equals("license"))
				{
					attribution.licenseURL = el.attr("href");
					attribution.licenseType = el.html();
				}					
			}
		
			Elements titleEl = doc.getElementsByTag("title");
			String pageTitle = titleEl.get(0).html();
			attribution.pageTitle = pageTitle.substring(0, pageTitle.indexOf(" – "));
			
			Elements propertyAttrs = doc.getElementsByAttribute("property");
			
			for (Element el : propertyAttrs)
			{
				if (el.attr("property").equals("cc:attributionName"))
				{
					attribution.author = el.html();
				}
					
			}
			
			attributionTxt.setText(attribution.getAttribution());
    
		}
		
		private void buildOpenStaxAttribution(Document doc, Attribution attribution)
		{
			Element pageTitleEl = doc.selectFirst("[class^=\"BookBanner__BookChapter\"]");
			attribution.pageTitle = pageTitleEl.text();
				
			Element bookTitleEl = doc.selectFirst("[class^=\"BookBanner__BookTitleLink\"]");
			attribution.bookTitle = bookTitleEl.text();
			attribution.bookURL = "https://openstax.org" + bookTitleEl.attr("href");
			
			attribution.author = "<a href=\'https://openstax.org/\'>OpenStax - Rice University</a>";
			
			Element licenseEl = doc.selectFirst("[data-html=\"copyright\"] > a");
			attribution.licenseType = licenseEl.text();
			attribution.licenseURL = licenseEl.attr("href");
			
			attributionTxt.setText(attribution.getAttribution());

		}
		
		private void buildManualAttribution(Attribution attribution)
		{
			attribution.pageTitle = pageTitleField.getText();
			attribution.bookTitle = bookTitleField.getText();
			attribution.bookURL = bookURLField.getText();
			attribution.author = authorField.getText();
			attribution.licenseType = licenseTypeField.getText();
			attribution.licenseURL = licenseURLField.getText();
			
			if (!bookTitleField.getText().isEmpty() && !bookURLField.getText().isEmpty() && !authorField.getText().isEmpty() &&
				!pageTitleField.getText().isEmpty() && !licenseTypeField.getText().isEmpty() && !licenseURLField.getText().isEmpty())
			{
				attributionTxt.setText(attribution.getAttribution());
			}
			else
			{
				attributionTxt.setText("Please fill out all the fields above.");;
			}
		}		
	}
	

	public static void main(String[] args)
	{
		
			new AttributionBuilder();
			
	}
}
//end class
