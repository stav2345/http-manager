package proxy;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.*;
import org.eclipse.swt.widgets.*;
import org.eclipse.swt.events.*;

public class ProxyLoginForm {
	Display display;
	Shell shell;
	Label label1, label2;
	Text username;
	Text password;
	Text text;
	String proxyUser;
	String proxyPass;

	public ProxyLoginForm() {
		display = Display.getDefault();

		display.syncExec(new Runnable() {
			public void run() {

				shell = new Shell(display, SWT.APPLICATION_MODAL | SWT.TITLE | SWT.BORDER | SWT.WRAP);
				// set the dialog layout
				shell.setLayout(new GridLayout(2, false));

				shell.setText("Proxy login form");
				label1 = new Label(shell, SWT.NULL);
				label1.setText("Proxy User Name: ");

				username = new Text(shell, SWT.SINGLE | SWT.BORDER);
				username.setText("");
				username.setTextLimit(30);

				label2 = new Label(shell, SWT.NULL);
				label2.setText("Proxy Password: ");

				password = new Text(shell, SWT.SINGLE | SWT.BORDER);
				password.setEchoChar('*');
				password.setTextLimit(30);

				Button button = new Button(shell, SWT.PUSH);
				button.setText("Submit");
				button.addListener(SWT.Selection, new Listener() {
					public void handleEvent(Event event) {
						proxyUser = username.getText();
						proxyPass = password.getText();
						boolean isValid = false;

						if (proxyUser == "") {
							MessageBox messageBox = new MessageBox(shell, SWT.OK | SWT.ICON_WARNING | SWT.CANCEL);
							messageBox.setMessage("Enter the User Name");
							messageBox.open();
						} else if (proxyPass == "") {
							MessageBox messageBox = new MessageBox(shell, SWT.OK | SWT.ICON_WARNING | SWT.CANCEL);
							messageBox.setMessage("Enter the Password");
							messageBox.open();
						} else
							isValid = true;

						if (isValid)
							shell.dispose();
					}
				});
				username.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
				password.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

				shell.pack();
				shell.open();

				while (!shell.isDisposed()) {
					if (!display.readAndDispatch()) {
						display.sleep();
					}
				}
			} // run
		});
	}

	public static void main(String[] args) {
		ProxyLoginForm plf = new ProxyLoginForm();
		System.out.println(plf.proxyUser + ":" + plf.proxyPass);
	}
}