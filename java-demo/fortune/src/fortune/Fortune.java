/**
 * Simple program to pull a quote from https://wjblack.com/quote
 */
package fortune;

// We need swing and bits of AWT to actually make a GUI.
// This isn't the only GUI toolkit available, BTW, just a handy one :-)
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

// These are needed to do the HTTP GET of the URL.
import java.io.*;
import java.net.*;


/**
 * Create a GUI program that uses a REST call to pull a fortune and
 * display it onscreen.
 * 
 * @author BJ Black <bj@wjblack.com>
 */
public class Fortune extends JFrame implements ActionListener {
	// Required (kinda) just to have some kind of version number.
	// Not strictly needed for compilation, but Eclipse prefers you
	// set SOMETHING here.
	private static final long serialVersionUID = 1L;

	// The button the user can click to fetch a fortune.
	public JButton btnFetch;
	
	/**
	 * The base constructor.  Use this when making a new Fortune window.
	 * @param title The title to give the window.
	 */
	public Fortune(String title) {
		// Invoke stuff from JFrame (our superclass).
		super(title);
		// Set the initial size of the window.
		setSize(640, 240);
		// When this window closes, terminate the program.
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	}
	
	/**
	 * The main routine.  Instantiate and run this GUI program.
	 * @param args The command-line arguments (ignored).
	 */
	public static void main(String[] args) {
		// We need to instandiate a dynamic copy of this object,
		// because we need a freshly-allocated copy to actually
		// run it.
		Fortune f = new Fortune("Today's Fortune");
		// Make a new button, put it in the Fortune GUI, and
		// add an action to do when we click it.
		f.btnFetch = new JButton("Fetch Fortune");
		f.btnFetch.addActionListener(f);
		f.getContentPane().add(f.btnFetch);
		// Start the GUI.
		f.setVisible(true);
	}

	/**
	 * Required by the ActionListener interface.  What happens when the
	 * user clicks the button?
	 * @param e The click (or whatever) event.
	 */
	public void actionPerformed(ActionEvent e) {
		Container panel = getContentPane();
		// Remove the button.
		panel.remove(btnFetch);
		// Let the user know what we're doing.  Ideally we'd get
		// something super-quick, but it's always possible that the
		// HTTP request will take a while.
		JTextArea content = new JTextArea("Getting fortune...");
		panel.add(content);
		panel.doLayout();
		panel.repaint();
		content.setText(fetch());
		panel.doLayout();
		panel.repaint();
	}

	/**
	 * Fetch the latest fortune from the server (or spit out an error).
	 * @return The fortune text, or an error.
	 */
	private String fetch() {
		// There are a LOT of places this can go wrong, so we are going
		// to see a TON of stuff that could cause exceptions.  In
		// all cases, if we get an exception, we just spit back a
		// text description of it.
		String ret = "";
		// This is our big try block.  Any number of places this thing
		// can go wrong network-wise, so here we go...
		try {
			URL url = new URL("https://wjblack.com/quote");
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			conn.setRequestMethod("GET");
			int retcode = conn.getResponseCode();
			if( retcode != 200 ) {
				ret = String.format("Got HTTP code %d when fetching.",
					retcode);
			} else {
				// Now we need to grab the stuff coming in from
				// the web request and return it to the caller.
				BufferedReader in = new BufferedReader(
					new InputStreamReader(conn.getInputStream())
				);
				String inputLine;
				StringBuffer response = new StringBuffer();

				// For every line of stuff we got, push it onto our
				// response.  Note that an IOException can happen
				// pretty much anywhere here, so it's nice to be able
				// to put a breakpoint anywhere in this code.
				while ((inputLine = in.readLine()) != null) {
					response.append(inputLine);
				}
				// If we got here OK, then we're done.  Close the
				// HTTP stream.
				in.close();

				// print result
				ret = "Today's fortune:\n\n" + response.toString();
			}
		} catch (MalformedURLException e) {
			ret = "Bad URL in code.  Got:\n" + e.getMessage();
		} catch (IOException e) {
			ret = "Error opening URL.  Got:\n" + e.getMessage();
		}
		return ret;
	}
}