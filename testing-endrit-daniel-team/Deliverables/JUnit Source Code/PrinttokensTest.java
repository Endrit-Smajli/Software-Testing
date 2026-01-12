import org.junit.*;
import static org.junit.Assert.*;
import java.io.*;
import java.nio.Buffer;
import javax.print.attribute.standard.PageRanges;


public class PrinttokensTest{
     
    @Test
    public void testTokenType(){
        //keywords
        assertEquals(Printtokens.keyword, Printtokens.token_type("and"));
        assertEquals(Printtokens.keyword, Printtokens.token_type("or"));
        assertEquals(Printtokens.keyword, Printtokens.token_type("xor"));
        assertEquals(Printtokens.keyword, Printtokens.token_type("lambda"));
        assertEquals(Printtokens.keyword, Printtokens.token_type("=>"));
        assertEquals(Printtokens.keyword, Printtokens.token_type("if"));

        //special symbols
        assertEquals(Printtokens.spec_symbol, Printtokens.token_type("("));
        assertEquals(Printtokens.spec_symbol, Printtokens.token_type(")"));
        assertEquals(Printtokens.spec_symbol, Printtokens.token_type("["));
        assertEquals(Printtokens.spec_symbol, Printtokens.token_type("]"));
        assertEquals(Printtokens.spec_symbol, Printtokens.token_type("'"));
        assertEquals(Printtokens.spec_symbol, Printtokens.token_type("`"));
        assertEquals(Printtokens.spec_symbol, Printtokens.token_type(","));

        //identifiers
        assertEquals(Printtokens.identifier, Printtokens.token_type("a"));
        assertEquals(Printtokens.identifier, Printtokens.token_type("b2"));
        assertFalse(Printtokens.is_identifier("2as"));
        assertFalse(Printtokens.is_identifier("2"));

        //number constants
        assertEquals(Printtokens.num_constant, Printtokens.token_type("1"));
        assertEquals(Printtokens.num_constant, Printtokens.token_type("45"));
        assertEquals(Printtokens.num_constant, Printtokens.token_type("255"));
        assertFalse(Printtokens.is_num_constant("as"));
        assertFalse(Printtokens.is_num_constant("a23s"));

        //string constants
        assertEquals(Printtokens.str_constant, Printtokens.token_type("\"abc\""));
        assertEquals(Printtokens.str_constant, Printtokens.token_type("\"123\""));
        assertFalse(Printtokens.is_str_constant("acv"));
        assertFalse(Printtokens.is_str_constant("132"));

        //comments
        assertEquals(Printtokens.comment, Printtokens.token_type(";Something goes here for now"));
        assertNotEquals(Printtokens.comment, Printtokens.token_type("This is a false comment!"));

        //character constants
        assertEquals(Printtokens.char_constant, Printtokens.token_type("#a"));
        assertFalse(Printtokens.is_char_constant("a"));
        assertFalse(Printtokens.is_char_constant("a#f"));
    }
    
    @Test
    public void testPrint_token(){
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        PrintStream origOut = System.out;
        System.setOut(new PrintStream(out));

        //Keywords
        Printtokens k = new Printtokens();
        k.print_token("and");
        assertEquals("keyword,\"and\".\n", out.toString());
        out.reset();

        //Identifiers
        Printtokens identify = new Printtokens();
        identify.print_token("aa");
        assertEquals("identifier,\"aa\".\n", out.toString());
        out.reset();

        //Number Constants
        Printtokens numConst = new Printtokens();
        numConst.print_token("1234");
        assertEquals("numeric,1234.\n", out.toString());
        out.reset();

        //String Constants
        Printtokens strConst = new Printtokens();
        strConst.print_token("\"asd\"");
        assertEquals("string,\"asd\".\n", out.toString());
        out.reset();

        //Character Constants
        Printtokens charConst = new Printtokens();
        charConst.print_token("#a");
        assertEquals("character,\"#a\".\n", out.toString());
        out.reset();

        //Comments
        Printtokens comment = new Printtokens();
        comment.print_token(";hello");
        assertEquals("comment,\";hello\".\n", out.toString());
        out.reset();

        //Special Symbols
        Printtokens specSym = new Printtokens();
        specSym.print_token("]");
        assertEquals("rsquare.\n", out.toString());
        out.reset();

        System.setOut(origOut);
    }

    @Test
    public void testIsComment(){
        //True cases
        assertTrue(Printtokens.is_comment(";this is a comment"));
        assertTrue(Printtokens.is_comment(";"));

        //False cases
        assertFalse(Printtokens.is_comment("This is a comment"));
        assertFalse(Printtokens.is_comment("a ; this is also a comment"));

        try{
            Printtokens.is_comment("");
            fail("Empty string not a comment");
        } catch (StringIndexOutOfBoundsException e){
            System.out.println("Null case passed!");
        }
    }

    @Test
    public void testIsKeyword(){
        String[] keywords = {"and", "or", "xor", "lambda", "=>", "if"};

        for(String keyWord : keywords){
            assertTrue(keyWord, Printtokens.is_keyword(keyWord));
        }

        assertFalse(Printtokens.is_char_constant(null));
    }

    @Test
    public void testIs_char_constant(){
        //True cases
        assertTrue(Printtokens.is_char_constant("#a"));
        
        //False cases
        assertFalse(Printtokens.is_char_constant("#abc"));
        assertFalse(Printtokens.is_char_constant("#"));
        assertFalse(Printtokens.is_char_constant("a"));
        assertFalse(Printtokens.is_char_constant("a#s"));
        assertFalse(Printtokens.is_char_constant(null));
    }

    @Test
    public void testIs_num_constant(){
        //True cases
        assertTrue(Printtokens.is_num_constant("123"));
        assertTrue(Printtokens.is_num_constant("1"));
        
        //False cases
        assertFalse(Printtokens.is_num_constant("12#4"));
        assertFalse(Printtokens.is_num_constant("1ad4"));
        assertFalse(Printtokens.is_num_constant("null"));
    }

    @Test
    public void testIs_str_constant(){
        //True Cases
        assertTrue(Printtokens.is_str_constant("\"abc\""));
        assertTrue(Printtokens.is_str_constant("\"123\""));

        //False cases
        assertFalse(Printtokens.is_str_constant("abc"));
        assertFalse(Printtokens.is_str_constant("123"));
        assertFalse(Printtokens.is_str_constant("null"));
    }

    @Test
    public void testIs_identifier(){
        //True cases
        assertTrue(Printtokens.is_identifier("a"));
        assertTrue(Printtokens.is_identifier("aaaa"));
        assertTrue(Printtokens.is_identifier("a12"));
        
        //False cases
        assertFalse(Printtokens.is_identifier("123"));
        assertFalse(Printtokens.is_identifier("1sa"));
        assertFalse(Printtokens.is_identifier("a12#d"));
    }

    @Test
    public void testIs_spec_symbol(){
        String[] specSym = {"(", ")", "[", "]", "'", "`", ","};

        for(String symbol : specSym){
            assertTrue(symbol, Printtokens.is_spec_symbol(symbol.charAt(0)));
        }
    }

    @Test
    public void testPrint_spec_sym(){
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        PrintStream origOut = System.out;
        System.setOut(new PrintStream(out));

        String[][] specCases = {
            {"(", "lparen.\n"},
            {")", "rparen.\n"},
            {"[", "lsquare.\n"},
            {"]", "rsquare.\n"},
            {"'", "quote.\n"},
            {"`", "bquote.\n"},
            {",", "comma.\n"}
        };
        
        for(String[] pair : specCases){
            out.reset();
            Printtokens.print_spec_symbol(pair[0]);
            assertEquals(pair[1], out.toString());
        }

        System.setOut(origOut);
    }

    @Test
    public void testIs_token_end(){
        //TRUE CASES
        //res==-1
        assertTrue(Printtokens.is_token_end(0, -1));
        assertTrue(Printtokens.is_token_end(1, -1));
        assertTrue(Printtokens.is_token_end(2, -1));
        //id = 0
        assertTrue(Printtokens.is_token_end(0, (int)'('));
        assertTrue(Printtokens.is_token_end(0, (int)')'));
        assertTrue(Printtokens.is_token_end(0, (int)' '));
        assertTrue(Printtokens.is_token_end(0, (int)';'));  
        //id = 1
        assertTrue(Printtokens.is_token_end(1, (int)'"'));
        assertTrue(Printtokens.is_token_end(1, (int)'\n'));
        assertTrue(Printtokens.is_token_end(1, (int)'\t'));
        //id = 2
        assertTrue(Printtokens.is_token_end(2, (int)'\n'));
        assertTrue(Printtokens.is_token_end(2, (int)'\t'));

        //FALSE CASES
        //index only seeing a character, not the end
        assertFalse(Printtokens.is_token_end(0, (int)'e'));
        assertFalse(Printtokens.is_token_end(1, (int)'e'));
        assertFalse(Printtokens.is_token_end(2, (int)'e'));
    }

    @Test
    public void testOpen_character_stream() throws IOException{
        Printtokens pt =new Printtokens();
        
        //Null file case
        BufferedReader readNull = pt.open_character_stream(null);
        assertNotNull("Return the reader from System.in when file name is null", readNull);
        readNull.close();
        
        //Incorrect file name (nonexisting file name)
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        PrintStream origOut = System.out;
        System.setOut(new PrintStream(out));

        BufferedReader readInv = pt.open_character_stream("non-existantfile.txt");
        assertNull("Should return null for incorrect file name", readInv);
        assertTrue("Print error message: ", out.toString().contains("doesn't exists\n"));

        //Correct file name
        File file = File.createTempFile("test", ".txt");
        FileWriter writer = new FileWriter(file);
        writer.write("this is a test");
        writer.close();

        BufferedReader readFile = pt.open_character_stream(file.getAbsolutePath());
        assertNotNull("Return the reader to existing file", readFile);
        assertEquals("this is a test", readFile.readLine());
        readFile.close();
        writer.close();

        System.setOut(origOut);
    }

    @Test
    public void testGet_char(){
        String input = "asd";
        BufferedReader reader = new BufferedReader(new StringReader(input));
        Printtokens text = new Printtokens();

        int idx1 = text.get_char(reader);
        assertEquals('a', idx1);

        int idx2 = text.get_char(reader);
        assertEquals('s', idx2);

        int idx3 = text.get_char(reader);
        assertEquals('d', idx3);

        int idx4 = text.get_char(reader);
        assertEquals(-1, idx4);
    }

    @Test
    public void testUnget_char() throws IOException{
        String input = "asd";
        BufferedReader reader = new BufferedReader(new StringReader(input));
        reader.mark(10);

        int idx1 = reader.read();
        assertEquals('a', idx1);

        Printtokens pt = new Printtokens();
        pt.unget_char(idx1, reader);

        int idx2 = reader.read();
        assertEquals("Reset takes us back to 'a'", 'a', idx2);
    }
    

    @Test 
    public void testOpen_token_stream() throws IOException{
        Printtokens pt = new Printtokens();
        
        //If the file name is null or " "
        BufferedReader read = pt.open_token_stream(null);
        assertNotNull("Return to System.in for null input", read);
        read.close();

        BufferedReader read2 = pt.open_token_stream("");
        assertNotNull("Return to System.in for empty input", read2);
        read2.close();

        //If the file name is invalid
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        PrintStream origOut = System.out;
        System.setOut(new PrintStream(out));

        BufferedReader read3 = pt.open_token_stream("non-existing file.txt");
        assertNull("Return to System.in for invalid file name input", read3);
        assertTrue("Print an error message", out.toString().contains("doesn't exist"));

        System.setOut(origOut);

        //If the file name is valid
        File file = File.createTempFile("test", ".txt");
        FileWriter writer = new FileWriter(file);
        writer.write("This is a test");
        writer.close();

        BufferedReader read4 = pt.open_token_stream(file.getAbsolutePath());
        assertNotNull("Return the reader for valid file", read4);
        assertEquals("This is a test", read4.readLine());
        read4.close();
        file.delete();
    }

    @Test
    public void testGet_token(){
        Printtokens pt = new Printtokens();

        String input = "This : is a ' ` test \n] comment";
        BufferedReader read = new BufferedReader(new StringReader(input));

        assertEquals("This", pt.get_token(read));
        assertEquals(":", pt.get_token(read));
        assertEquals("is", pt.get_token(read));
        assertEquals("a", pt.get_token(read));
        assertEquals("'", pt.get_token(read));
        assertEquals("`", pt.get_token(read));
        assertEquals("test", pt.get_token(read));
        assertEquals("]", pt.get_token(read));
        assertEquals("comment", pt.get_token(read));
        assertNull(pt.get_token(read));

        String input2 = "This is a test comment";
        BufferedReader read2 = new BufferedReader(new StringReader(input2));

        assertEquals("This", pt.get_token(read2));
        assertEquals("is", pt.get_token(read2));
        assertEquals("a", pt.get_token(read2));
        assertEquals("test", pt.get_token(read2));
        assertEquals("comment", pt.get_token(read2));
        assertNull(pt.get_token(read2));
    }
}