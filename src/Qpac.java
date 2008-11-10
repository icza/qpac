
import classes.MainFrame;


/**
    <HR>
    <TABLE>
    <TR><TD>Author: <TD>Belicza Andras
    <TR><TD>E-mail: <TD><A href="mailto:icza@freemail.hu">icza@freemail.hu</A>
    <TR><TD>Program title: <TD>Qpac
    <TR><TD>Home page: <TD><A href="http://icza.sch.bme.hu">http://icza.sch.bme.hu</A>"
    <TR><TD>Programming language: <TD> Java2
    <TR><TD>Software Development Kit: <TD> Sun Java 2 Software Development Kit v1.4.2 (<A href="http://java.sun.com">java.sun.com</A>)
    <TR><TD>Text editor: <TD> UltraEdit-32 10.0c (<A href="http://www.ultraedit.com">www.ultraedit.com</A>)
    </TABLE>
    <HR>
    If you have any comments, please visit the home page!
    If you have any questions, bug reports, job offers or money to support me, please, contact me!<BR>
    If you have new Qpac graphics files, upload them at the home page!
    The program is freeware.<BR>
    The source code is now public!<BR>
    You can copy or modify the source code of the program without permission but you have to indicate all modifications.<BR>
    <BR>
    2003<BR>
    Hungary<BR>
    <HR>
    <BR>
    This is the main class of Qpac. This will create the main frame, and will make it visible.
    @author Belicza Andras
*/
public class Qpac {

    /**
        The entry point of the program.
        @param arguments used to take parameters from the running environment
    */
    public static void main( final String[] arguments ) {
        new MainFrame();
    }

}
