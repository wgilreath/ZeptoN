/*
 * @(#)Zep.java
 *
 * Title: Zep - ZeptoN Transcompiler.
 *
 * Description: A ZeptoN transcompiler using the Java Compiler API with options not in javac.
 *
 * @author William F. Gilreath (wfgilreath@yahoo.com)
 * @version 1.0  8/15/19
 *
 * Copyright © 2019 All Rights Reserved.
 *
 * License: This software is subject to the terms of the GNU General Public License (GPL)  
 *     version 3.0 available at the following link: http://www.gnu.org/copyleft/gpl.html.
 *
 * You must accept the terms of the GNU General Public License (GPL) license agreement
 *     to use this software.
 *
 **/

import java.io.PrintWriter;
import java.io.Writer;
import java.net.URI;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import java.util.Arrays;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

import javax.tools.Diagnostic;
import javax.tools.DiagnosticCollector;
import javax.tools.JavaCompiler;
import javax.tools.JavaFileObject;
import javax.tools.SimpleJavaFileObject;
import javax.tools.StandardJavaFileManager;
import javax.tools.ToolProvider;

public final class Zep {

    /**
     * Java source code object as name and text as String objects. Used by the Java Compiler API 
     * to compile the transpiled ZeptoN source code in Java to a Java .class bytecode file.
     */
	static class JavaSourceCodeStringObject extends SimpleJavaFileObject {

		//source code in Java transpiled from ZeptoN
	    private final String code;

	    /**
	     * Constructor to create a Java source code object used by the Java Compiler API.
	     *
	     * @param name  - name of the Java class for the Java source code.
	     * @param code	- code text of the Java source code.
	     */
	    public JavaSourceCodeStringObject(final String name, final String code) {
	        super(URI.create("string:///" + name.replace('.','/') + Kind.SOURCE.extension), Kind.SOURCE);
	        this.code = code;
	    }//end constructor

	    /**
	     * Get the source Java code as a character sequence.
	     *
	     * @param ignoreEncodingErrors - a boolean flag to ignore problems with the encoding of the source code.
	     *      
	     * @return CharSequence - the general character sequence type as an interface.
	     */
	    @Override
	    public CharSequence getCharContent(boolean ignoreEncodingErrors) {
	        return code;
	    }//end getCharContent

	    /**
	     * Get the source Java code as a Java string.
	     *
	     * @return String - the Java source code as a String object.
	     */
	    public String getCode(){ return this.code; }
	    
	}//end class JavaSourceCodeStringObject
	
	//predefined imports used in the transcompile of ZeptoN to Java source code.
    private final static String HEAD =  

    	  "import java.io.*; "+
          "import java.math.*; " +
    	  "import java.net.*; "+
    	  "import java.util.*; "+
          " ";
    
    //predefined environment methods used in the transcompile of ZeptoN to Java source code.
    private final static String BODY =

        "private final static char[] EMPTY_CHAR = new char[0]; "+
        "private final static String EMPTY_STRING = new String(); "+
        "private final static char NULL_CHAR = Character.MIN_VALUE; "+
        "private static String[] argv = new String[0]; "+
        "private final static PrintStream out_str = System.out; "+  
        "private final static InputStream inp_str = System.in; "+   
        "private final static PrintStream err_str = System.err; "+  
        "private final static Console  con = System.console(); "+
        "private final static Runtime  run = Runtime.getRuntime(); "+
        "private final static Scanner  scan = new Scanner(System.in); "+

        "private final static void init(final String[] args){argv=args;}"+
        "private final static void deinit(){try{out_str.flush();out_str.close();err_str.flush();err_str.close();inp_str.close();}catch (Exception ex){err_str.println(ex.getMessage());ex.printStackTrace(err_str);}}"+
     
        "private final static String readLine(final String fmt, final Object... args){if(con==null){return EMPTY_STRING;} return con.readLine(fmt,args);} "+
        "private final static char[] readPassword(String fmt,Object... args){if (con==null){return EMPTY_CHAR;} return con.readPassword(fmt,args);} "+
        "private final static char[] readPassword(){if(con==null){return EMPTY_CHAR;} return con.readPassword();} "+
        "private final static void gc(){run.gc();}"+
        "private final static BigDecimal readBigDecimal(){return scan.nextBigDecimal();}"+
        "private final static BigInteger readBigIntegr(){return scan.nextBigInteger();}"+
        "private final static byte readByte(){return scan.nextByte();}"+
        "private final static int readInt(){return scan.nextInt();}"+
        "private final static long readLong(){return scan.nextLong();}"+
        "private final static short readShort(){return scan.nextShort();}"+
        "private final static double readDouble(){return scan.nextDouble();}"+
        "private final static float readFloat(){return scan.nextFloat();}"+
        "private final static String readString(){try{return scan.next();}catch (Exception ex){err_str.println(ex.getMessage());ex.printStackTrace(err_str);} return EMPTY_STRING;} "+
        "private final static void printf(final String fmt,final Object... param){out_str.printf(fmt,param);} "+
        "private final static void print(final char[] param){out_str.print(param);}"+
        "private final static void print(final BigDecimal param){out_str.print(param.toPlainString());}"+
        "private final static void print(final BigInteger param){out_str.print(param.toString());}"+
        "private final static void print(final boolean param){out_str.print(param);}"+
        "private final static void print(final byte param){out_str.print(param);}"+
        "private final static void print(final char param){out_str.print(param);}"+
        "private final static void print(final double param){out_str.print(param);}"+
        "private final static void print(final float param){out_str.print(param);}"+
        "private final static void print(final int param){out_str.print(param);}"+
        "private final static void print(final long param){out_str.print(param);}"+
        "private final static void print(final Object param){out_str.print(param);}"+
        "private final static void print(final short param){out_str.print(param);}"+
        "private final static void print(final String param){out_str.print(param);}"+
        
        "private final static void println(){out_str.println();}"+
        "private final static void println(final char[] param){out_str.println(param);} "+
        "private final static void println(final BigDecimal param){out_str.print(param.toPlainString());}"+
        "private final static void println(final BigInteger param){out_str.print(param.toString());}"+
        "private final static void println(final boolean param){out_str.println(param);} "+
        "private final static void println(final byte param){out_str.println(param);}"+
        "private final static void println(final char param){out_str.println(param);}"+
        "private final static void println(final double param){out_str.println(param);}"+
        "private final static void println(final float param){out_str.println(param);}"+
        "private final static void println(final int param){out_str.println(param);}"+
        "private final static void println(final long param){out_str.println(param);}"+
        "private final static void println(final Object param){out_str.println(param);}"+
        "private final static void println(final short param){out_str.println(param);}"+
        "private final static void println(final String param){out_str.println(param);}"+
        "private final static void exit(final int code){run.exit(code);}"+
        "private final static long freeMemory(){return run.freeMemory();}"+
        "private final static long maxMemory(){return run.maxMemory();}"+
        "private final static long totalMemory(){return run.totalMemory();}"+
        "private final static long currentTimeMillis(){return System.currentTimeMillis();}"+
        "private final static long nanoTime(){return System.nanoTime();}"+
        "private final static boolean readBoolean(){return scan.nextBoolean();}"+
        "private final static char readChar(){char chr;try{chr = (char) inp_str.read();}catch (Exception ex){chr = NULL_CHAR;} return chr;}"+
        "private final static String readLine(){String line = EMPTY_STRING;try{line = scan.nextLine();}catch (Exception ex){line = EMPTY_STRING;} return line;}"+
        "private final static Locale getLocale(){return scan.locale();}"+
        "private final static void arraycopy(final Object src,final int srcPos,Object dst,final int dstPost,final int len){System.arraycopy(src,srcPos,dst,dstPost,len);} "+
        "private final static String getenv(final String param){return System.getenv(param);}"+
        "private final static int identityHashCode(final Object obj){return System.identityHashCode(obj);}"+
        "private final static String getProperty(final String param){return System.getProperty(param);}"+
        "private final static Runtime getRuntime(){return run;}"+
        "private final static String toString(final boolean[] param){return Arrays.toString(param);}"+
        "private final static String toString(final byte[] param){return Arrays.toString(param);}"+
        "private final static String toString(final char[] param){return Arrays.toString(param);}"+
        "private final static String toString(final double[] param){return Arrays.toString(param);}"+
        "private final static String toString(final float[] param){return Arrays.toString(param);}"+
        "private final static String toString(final int[] param){return Arrays.toString(param);}"+
        "private final static String toString(final long[] param){return Arrays.toString(param);}"+
        "private final static String toString(final short[] param){return Arrays.toString(param);}"+
        "private final static String toString(final Object[] param){return Arrays.toString(param);}"+
        "private final static String valueOf(final char[] param){return String.valueOf(param);}"+
        "private final static Console console(){return con;}"+  
        "private final static String[] getArgs(){return argv;}"+ 
        "private final static void errorf(final String fmt,final Object...param){err_str.printf(fmt,param);}"+
        "private final static void nop(){;}"+
        " ";

    private static boolean briefFlag = false;  //set brief error reporting a count of error diagnostics
    private static boolean finalFlag = false;  //set final compilation with no debug information

    private static boolean echoFlag = false;  //set echo ZeptoN compiler parameters and compiler status
    private static boolean hushFlag = false;  //set hush compiler diagnostics except errors
    private static boolean muteFlag = false;  //set mute all compiler diagnostics are silenced
    private static boolean timeFlag = false;  //set to time overall time to compile a ZeptoN source file

    private final static ArrayList<String> files = new ArrayList<>(); //Javac compiler ZeptoN source files
    private final static ArrayList<String> param = new ArrayList<>(); //Javac compiler parameters implicit and explicit

    //internal constants used by the compiler
    private final static Charset CHARSET      = Charset.defaultCharset();
    private final static String  ENDLN        = System.getProperty("line.separator");
    private final static Locale  LOCALE       = Locale.getDefault();
    private final static Writer  SYS_ERR      = new PrintWriter(System.err, true);
    private final static String  EMPTY_STRING = "";

    private final static Iterable<String> NO_ANNOTATION_PROC = Collections.emptyList();

    /**
     * Private constructor to prevent instantiating this class except internally.
     */
    private Zep() {
    }//end constructor

    /**
     * Diagnose compiler errors with error, position, and illustrative source code line.
     *
     * @param fileName     - name of the external file containing the ZeptoN source code.
     * @param diagnostics  - diagnostic information from compile of Java source code.
     * @param javaFileCode - list containing the lines of Java/ZeptoN source code from file.
     */
    private void diagnose(final String fileName,
                          final DiagnosticCollector<JavaFileObject> diagnostics,
                          final List<String> javaFileCode) {

        for (Diagnostic<?> diag : diagnostics.getDiagnostics()) {

            if (hushFlag) {
                if (diag.getKind() != Diagnostic.Kind.ERROR)
                    continue;
            }//end if

            final String diagnosticText = diag.toString();

            System.out.printf("Error: %s.%n", fileName);

            if (diag.getKind() != Diagnostic.Kind.NOTE) {

                System.out.printf("Line %d ", diag.getLineNumber());
                System.out.printf("At %d:", diag.getColumnNumber());

                String diagText = diagnosticText.split(":")[3];
                String diagLine = diagText.split(ENDLN)[0];

                System.out.printf("%s%n", diagLine);

                String codeLine = this.getCodeLine(javaFileCode, diag.getLineNumber(), diag.getColumnNumber());
                System.out.printf("%s%n", codeLine);

            } else {
                System.out.println(diag.getMessage(LOCALE));
            }//end if

            System.out.println();

        }//end for

    }//end diagnose

    /**
     * Get the source line of Java/ZeptoN source code and format with indicator of the point of diagnostic error.
     *
     * @param srcCode - a list containing the Java/ZeptoN source code.
     * @param lineNum - the line number within the Java/ZeptoN source code to retrieve.
     * @param colNum  - the column position within the line for the diagnostic error.
     * @return String - the line of source code formatted to indicate point of error.
     */
    private String getCodeLine(final List<String> srcCode, final long lineNum, final long colNum) {

        try {

            String line = srcCode.get((int) lineNum - 1);

            StringBuilder codeLine = new StringBuilder(line);
            codeLine.append(ENDLN);

            for (int x = 0; x < colNum - 1; x++) {
                codeLine.append(" ");
            }//end for

            codeLine.append("^");

            return codeLine.toString();

        } catch (Exception ex) {
            error("getCodeLine() Exception: '%s' is '%s'.%n", ex.getClass().getName(), ex.getMessage());
        }//end try

        return EMPTY_STRING;

    }//end getCodeLine

    /**
     * Compile single ZeptoN source code file using the Java API with compiler parameters.
     *
     * @param fileName - name of the external file containing the ZeptoN source code.
     */
    private void compileZeptoN(final JavaSourceCodeStringObject zepSrc, final String fileName) {

        if (echoFlag) {

            System.out.printf("%nZeptoN Compiler Options: %s Files: %s Encoding: %s%n%n",
                    param.isEmpty() ? "None." : param.toString(), files.toString(), CHARSET);

        }//end if

        //0 - error, 1 - mandatory warning, 2 - note, 3 - other, 4 - warning, 5 - total diagnostic
        final int[] diagnosticCounter = new int[]{0, 0, 0, 0, 0, 0};

        boolean statusFlag = false;

        long timeStart = 0, timeClose = 0;

        try {

            Iterable<? extends JavaFileObject> list = Arrays.asList(zepSrc);


            JavaCompiler                       	comp = ToolProvider.getSystemJavaCompiler();
            DiagnosticCollector<JavaFileObject> diag = new DiagnosticCollector<>();
            StandardJavaFileManager 			file = comp.getStandardFileManager(diag, LOCALE, CHARSET);
            JavaCompiler.CompilationTask 		task = comp.getTask(SYS_ERR,
                    file,
                    diag,
                    param,
                    NO_ANNOTATION_PROC,
                    list);

            System.gc();

            timeStart  = System.currentTimeMillis();
            statusFlag = task.call();
            timeClose  = System.currentTimeMillis();

            if (!muteFlag) {

                if (briefFlag && !statusFlag) {

                    for (Diagnostic<? extends JavaFileObject> diagnostic : diag.getDiagnostics()) {

                        Diagnostic.Kind kind = diagnostic.getKind();

                        switch (kind) {
                            case ERROR:
                                diagnosticCounter[0]++;
                                diagnosticCounter[5]++;
                                break;
                            case MANDATORY_WARNING:
                                diagnosticCounter[1]++;
                                diagnosticCounter[5]++;
                                break;
                            case NOTE:
                                diagnosticCounter[2]++;
                                diagnosticCounter[5]++;
                                break;
                            case OTHER:
                                diagnosticCounter[3]++;
                                diagnosticCounter[5]++;
                                break;
                            case WARNING:
                                diagnosticCounter[4]++;
                                diagnosticCounter[5]++;
                                break;
                        }//end switch

                    }//end for

                }//end if (briefFlag)

                List<String> javaFileCode= new ArrayList<String>(Arrays.asList(zepSrc.getCode().split("\n")));

                if (!briefFlag && !statusFlag) this.diagnose(fileName, diag, javaFileCode);
                
                System.gc();

            }//end if (!Zep.muteFlag)

            file.close();

        } catch (Exception ex) {

            System.out.printf("ZeptoN Compiler Exception: '%s' is '%s'.%n", ex.getClass().getName(), ex.getMessage());

        } finally {

            if (briefFlag) {
                if (diagnosticCounter[5] > 0) {
                    System.out.printf("%3d Diagnostic messages:%n", diagnosticCounter[5]);
                    for (int x = 0; x < diagnosticCounter.length - 1; x++) {
                        if (diagnosticCounter[x] > 0) {

                            //0 - error, 1 - mandatory warning, 2 - note, 3 - other, 4 - warning, 5 - total diagnostic

                            switch (x) {
                                case 0:
                                    System.out.printf("  %3d Error%n", diagnosticCounter[x]);
                                    break;
                                case 1:
                                    System.out.printf("  %3d Mandatory Warning%n", diagnosticCounter[x]);
                                    break;
                                case 2:
                                    System.out.printf("  %3d Note%n", diagnosticCounter[x]);
                                    break;
                                case 3:
                                    System.out.printf("  %3d Other%n", diagnosticCounter[x]);
                                case 4:
                                    System.out.printf("  %3d Warning%n", diagnosticCounter[x]);
                                    break;
                            }//end switch

                        }//end if

                    }//end for
                    System.out.println();
                } else {
                    System.out.println("No compiler diagnostic messages.");
                }//end if
            }//end if( briefFlag )

            if (timeFlag) {
                System.out.printf("Time: %d-ms for: %s%n", (timeClose - timeStart), fileName);
            }//end if

            if (echoFlag) {
                System.out.printf("ZeptoN Compiler result for file: '%s' is: ", fileName);
                System.out.printf("%s%n", statusFlag ? "Success." : "Failure!");
                //System.exit(statusFlag ? EXIT_CODE_SUCCESS : EXIT_CODE_FAILURE);
            }//end if

        }//end try

        //if(statusFlag) createPackageDir(JavaSourceCodeString) //create package directory structure and move .class for -pack option??
        
    }//end compileZeptoN

    /**
     * Report a compiler error and then exit with status code of failure with a problem.
     *
     * @param text -  error message to report to the user.
     * @param args -  error message arguments to report.
     */
    private static void error(final String text, final Object... args) {

        System.out.printf("%nError! ");
        System.out.printf(text, args);
        System.out.printf("%n%n");
        System.exit(EXIT_CODE_PROBLEM);

    }//end error

    /**
     * Print compiler USEINFO and OPTIONS and then exit without invoking compiler.
     */
    private static void printOptions() {

        System.out.printf("%n%s%n%s%n", USEINFO, OPTIONS);
        System.exit(EXIT_CODE_SUCCESS);

    }//end printOptions

    /**
     * Print compiler RELEASE and VERSION and then exit without invoking compiler.
     */
    private static void printVersion() {

        System.out.printf("%s%n%s%n", RELEASE, VERSION);
        System.exit(EXIT_CODE_SUCCESS);

    }//end printVersion

    /**
     * Add any Javac compiler parameters to the compiler parameters passed to the Java Compiler API.
     *
     * @param args -  command line arguments for the ZeptoN compiler passed to Java Compiler API.
     * @param idx  -  starting index position within array of command line arguments.
     * @return int -  closing index position within the array of command line arguments.
     */
    private int processJavacArguments(final String[] args, final int idx) {

        int pos;

        for (pos = idx + 1; pos < args.length; pos++) {

            if (args[pos].contains(FILE_SOURCE_EXT)) {
                break;
            } else {
                param.add(args[pos]);
            }//end if

        }//end for

        return pos - 1;

    }//end processJavacArguments

    /**
     * Process the command line arguments to set the internal parameters for compilation.
     *
     * @param args - command line arguments to set compiler parameters during compilation.
     */
    private void processCommandLineArgs(final String[] args) {

        int x; //external for-loop index used after exiting the loop structure
        for (x = 0; x < args.length; x++) {

            if (args[x].contains("-")) {

                switch (args[x]) {

                    case "-time":
                        timeFlag = true;
                        break;
                    case "-echo":
                        echoFlag = true;
                        break;
                    case "-final":
                        finalFlag = true;
                        break;
                    case "-brief":
                        if (hushFlag || muteFlag)
                            error(ERROR_OPT_BRIEF);
                        briefFlag = true;
                        break;
                    case "-hush":
                        if (briefFlag || muteFlag)
                            error(ERROR_OPT_HUSH);
                        hushFlag = true;
                        break;
                    case "-mute":
                        if (briefFlag || hushFlag)
                            error(ERROR_OPT_MUTE);
                        muteFlag = true;
                        break;
                    case "-javac":
                        x = processJavacArguments(args, x);
                        break;
                    case "-help":
                        printOptions();
                        break;
                    case "-info":
                        printVersion();
                        break;
                    default:
                        error(ERROR_PARAM_WRONG, args[x]);
                        break;
                }//end switch

            } else {

                for (; x < args.length; x++) {

                    if (args[x].contains(FILE_SOURCE_EXT)) {
                        files.add(args[x]);
                    } else {

                        if (args[x].contains("-")) {
                            error(ERROR_PARAM_FILES, args[x]);
                        } else {
                            error(ERROR_FILE_EXTEN, args[x]);
                        }//end if

                    }//end if

                }//end for

                break;

            }//end if

        }//end for

    }//end processCommandLineArgs

    /**
     * Compile using the command line arguments of compiler parameters and ZeptoN source files.
     *
     * @param args - command line arguments passed to the ZeptoN transcompiler.
     */
    private static void compile(final String[] args) {

        if (args.length == 0) {
            error(ERROR_NO_INPUT);
        }//end if

        final Zep comp = new Zep();

        comp.processCommandLineArgs(args);

        if (files.isEmpty()) {
            error(ERROR_NO_FILES);
        }//end if

        comp.configureParams();

        for (String sourceFile : files) {
            comp.compileZeptoN(transpile(sourceFile), sourceFile);
        }//end for

    }//end compile

    private final static String JAVAC_FINAL = "-g:none";
    private final static String JAVAC_DEBUG = "-g";

    /**
     * Configure underlying Javac compiler parameters the WEJAC parameters passed as command line arguments.
     */
    private void configureParams() {

        for (String sourceFile : files) {
            verifyFile(sourceFile);
        }//end for

        param.add(finalFlag ? JAVAC_FINAL : JAVAC_DEBUG);
                
    }//end configureParams

    /**
     * Verify a ZeptoN source file in given path exists, is readable, and is of minimum file size to compile.
     *
     * @param filePath - file path to ZeptoN source file to compiler.
     */
    private static void verifyFile(final String filePath) {

        Path path = Paths.get(filePath);

        try {

            if (!Files.exists(path)) {
                error(ERROR_FILE_EXIST, path.toString());
            } else if (!Files.isReadable(path)) {
                error(ERROR_FILE_READ, path.toString());
            } else if (Files.size(path) < FILE_SIZE_MIN) {
                error(ERROR_FILE_SMALL, path.toString());
            }//end if

        } catch (Exception ex) {
            error("Verify File Exception: '%s' is '%s'.%n", ex.getClass().getName(), ex.getMessage());
        }//end try

    }//end verifyFile

    /**
     * Transcompile single ZeptoN source code file into Java source code for compilation
     *
     * @param  fileName                  - name of the external file containing the ZeptoN source code.
     * @return JavaSouceCodeStringObject - subclass of SimpleJavaFileObject that is the name and code of the ZeptoN source code.
     */

    private static JavaSourceCodeStringObject transpile(final String fileName) {

    	System.gc();
    	
    	String packageName = "";
        String programName = "";
        JavaSourceCodeStringObject javaObject = null;

        try {
            String zepSource = new String(Files.readAllBytes(Paths.get(fileName)), Zep.CHARSET);

            //check for 'prog'
            //check for 'begin' ??
            
            zepSource = zepSource.replaceAll("\\{", " {");
            
            //check for main method, change main method to mangled name
            if (zepSource.contains("static void main")) {
                zepSource = zepSource.replace("main(", "_$main(");
            }//end if

            StringBuilder javaSource = new StringBuilder(zepSource);

            boolean hasPackageName = false;
            if (zepSource.contains("package")) {
                int name = javaSource.indexOf("package");
                int semi = javaSource.indexOf(";");
                packageName = javaSource.substring(name + 8, semi);
                hasPackageName = true;
            }//end if

            int progIdent = javaSource.indexOf("prog");
            int openBrace = javaSource.indexOf("{");

            programName = javaSource.substring(progIdent + 5, openBrace - 1);
            programName = programName.trim();
            
            javaSource.insert(progIdent, Zep.HEAD);


            int tailBrace = javaSource.lastIndexOf("}");
            javaSource.replace(tailBrace, tailBrace+1, "}catch(Exception _$ex){ System.out.printf(\"Uncaught ZeptoN Program Exception: '%s' is '%s'.%n\", _$ex.getClass().getName(), _$ex.getMessage()); }finally{ deinit(); System.exit(0);}  } }");

            String javaCode = javaSource.toString();

            javaCode = javaCode.replace("prog", "public final class");
            javaCode = javaCode.replace("begin", Zep.BODY+" private "+programName+"(){} public static void main(String[] _$args){ try { init(_$args); ");

            if(hasPackageName) {
            	javaObject = new JavaSourceCodeStringObject(programName, javaCode);
            } else {
            	javaObject = new JavaSourceCodeStringObject(packageName+"."+programName, javaCode);
            }//end if
                        
        } catch (Exception ex) {
        	ex.printStackTrace();
            error("Transcompile Exception: '%s' is '%s'.%n", ex.getClass().getName(), ex.getMessage());
        }//end try

        return javaObject;

    }//end transpile

    private final static int EXIT_CODE_SUCCESS = 0; //success - compiler success in compiling ZeptoN source file.
    private final static int EXIT_CODE_FAILURE = 1; //failure - compiler failure in compiling ZeptoN source file.
    private final static int EXIT_CODE_PROBLEM = 2; //problem - compiler failure with a problem for ZeptoN source file.

    private final static long   FILE_SIZE_MIN   = 15;     //smallest ZeptoN file size is 15-bytes
    private final static String FILE_SOURCE_EXT = ".zep"; //ZeptoN source file extension

    private final static String ERROR_NO_INPUT = "No compiler options or files given! Use -help for options.";
    private final static String ERROR_NO_FILES = "No source files given! Use -help for options.";

    private final static String ERROR_PARAM_FILES = "Zep option: '%s' must precede ZeptoN source code files list.";
    private final static String ERROR_PARAM_WRONG = "Zep option: '%s' is not recognized.";

    private final static String ERROR_FILE_EXTEN = "File: '%s' does not have '.zep' extension.";
    private final static String ERROR_FILE_EXIST = "File: '%s' does not exist.";
    private final static String ERROR_FILE_READ  = "File: '%s' is unreadable.";
    private final static String ERROR_FILE_SMALL = "File: '%s' is too small.";

    private final static String ERROR_OPT_BRIEF = "Option -brief ambiguous with option -hush and/or -mute option.";
    private final static String ERROR_OPT_HUSH  = "Option -hush ambiguous with option -brief and/or -mute option.";
    private final static String ERROR_OPT_MUTE  = "Option -mute ambiguous with -brief and/or -hush option.";

    private final static String LICENSE = "License is GNU General Public License (GPL) version 3.0";
    private final static String VERSION = "Version 1.0 Released August 2019";

    private final static String RELEASE = "Zep - ZeptoN Echo Transcompiler\n(C) Copyright 2019 William F. Gilreath. All Rights Reserved";
    private final static String USEINFO = "Usage:  zep (option)* [ -javac (javac-options)+ ] (ZeptoN-file)+ | ( -help | -info )";

    private final static String OPTIONS = 
    		"                                                                        \n\r" +
            "  ZeptoN Compiler OPTIONS:                                                \n" +
            "                                                                          \n" +
            "  Compiler Options:  [ -echo ] | [ -final ] | [ -time ]                   \n" +
            "                                                                          \n" +
            "    -echo        Print ZeptoN compiler options and success or failure.    \n" +
            "    -final       Compile final release without debug information.         \n" +
            "    -time        Print total time for success compiling of a source file. \n" +
            "                                                                          \n" +

            "  Error Reporting Option: [ -brief | -hush | -mute ]                      \n" +
            "                                                                          \n" +
            "    -brief       Print only a brief count of compiler messages.           \n" +
            "    -hush        Disable all compiler messages except errors.             \n" +
            "    -mute        Disable all compiler messages.                           \n" +
            "                                                                          \n" +

            "  Help or Version Option:  ( -help | -? ) | ( -info | -v )                \n" +
            "                                                                          \n" +
            "    -help        Print list of compiler options and exit.                 \n" +
            "    -info        Print compiler version information and exit.             \n" +
            "                                                                          \n" +
            "  Note: All options for -javac are passed as-is to the compiler.          \n" +
            "                                                                        \n\r" ;

    /**
     * The main method is the central start method of the ZeptoN compiler that invokes other compiler methods.
     *
     * @param args - command-line arguments to compiler
     */
    public static void main(final String[] args) {

    	System.gc();
    	try {
	    	  	
	        if (args.length == 0) {
	            System.out.printf("%s %s%n%s%n", RELEASE, VERSION, LICENSE);
	        }//end if
	
	        compile(args);

    	} catch(Exception ex) {
    		
            error("ZeptoN Compiler Exception: '%s' is '%s'.%n", ex.getClass().getName(), ex.getMessage());
    		ex.printStackTrace();
    		System.exit(EXIT_CODE_FAILURE);
    	}//end try
    	
    	System.exit(EXIT_CODE_SUCCESS);
    	
    }//end main

}//end class Zep