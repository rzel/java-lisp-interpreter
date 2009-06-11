/**
 * LISP S-Expression
  * @author Keith Johansen
  * johansek@cse.ohio-state.edu
 */
import java.io.*;

public class SExp
{

    private SExp car;
    private SExp cdr;
    private String value;
    private boolean isAtomic;
    public final static SExp T = new SExp("T");
    public final static SExp NIL = new SExp("NIL");

    //for the tokenizer functions
    private static String pushedToken = null;

    
    /**
     * Need an empty constructor
     */
    SExp()
    {
        //nothing to do here
    }

    /**
     * Constructor for atom
     * @param aVal
     */
    SExp(String aVal)
    {
        value = aVal;
        isAtomic = true;
        car = null;
        cdr = null;
    }


    /**
     * Get the S expression in dot notation
     * @return dot notation
     */
    public String ToStringDotNotation()
    {
        if (isAtomic)
        {
            return this.value;
        }
        else
        {
            return "("+car.ToStringDotNotation()+" . "+cdr.ToStringDotNotation()+")";
        }
    }



    /**
     * Get the S Expression in list notation
     * This function is a little off
     * @return
     */
    public String ToStringListNotation()
    {
        String str;

        if (isAtomic)
        {
            str = value;
        } else
        {
            str = "(";
            str += car.ToStringListNotation();
            if (!cdr.IsNull())
            {
                if (!cdr.IsAtomic())
                {
                    String cdrStr = cdr.ToStringListNotation();
                    str += " " + cdrStr.substring(1, cdrStr.length() - 1);
                } else
                {
                    str += " . ";
                    str += cdr.ToStringListNotation();
                }
            }
            str += ")";
        }
        return str;
    }

    /**
     * Overloaded Default OUTPUT
     * @return the string to output
     */
    public String OUTPUT()
    {
        return this.OUTPUT(true);
    }

    /**
     * Controllabel output
     * @param dot True to use dot notation
     * @return The string to output
     */
    public String OUTPUT(boolean dot)
    {
        if(dot)
        {
            return this.ToStringDotNotation();
        }
        else
        {
            return this.ToStringListNotation();
        }
    }

    /**
     * Set the S expression as atomic
     * @param isAtom
     */
    private void SetAtom(boolean isAtom)
    {
        this.isAtomic = isAtom;
    }

    /**
     * Set the cdr
     * @param cdr  The value of the cdr
     */
    private void SetCDR(SExp cdr)
    {
        this.cdr = cdr;
    }

    /**
     * Get the CDR
     * @return The CDR
     */
    public SExp CDR()
    {
        return cdr;
    }

    /**
     * Set the car
     * @param car The value for the car
     */
    private void SetCAR(SExp car)
    {
        this.car = car;
    }

    /**
     * Get the car
     * @return The car
     */
    public SExp CAR()
    {
        return car;
    }



    /**
     * LISP built in CONS
     * @param s1
     * @param s2
     * @return CONSed S expression
     */
    public static SExp CONS(SExp s1, SExp s2)
    {
        SExp s = new SExp();
        s.SetCAR(s1);
        s.SetCDR(s2);
        return s;
    }

    /**
     * Get the value of an atomic s expression
     * @return The value
     */
    public String GetValue()
    {
        return value;
    }

    /**
     * Set the value of an atomic S expression
     * @param value The new value
     */
    public void SetValue(String value)
    {
        this.value = value;
    }

    /**
     * LISP built in add
     * @param s1
     * @param s2
     * @return
     * @throws java.lang.Exception
     */
    public static SExp PLUS(SExp s1, SExp s2) throws Exception
    {
        try
        {
            if (s1.IsAtomic() && s2.IsAtomic())
            {
                int n1 = Integer.parseInt(s1.GetValue());
                int n2 = Integer.parseInt(s2.GetValue());
                return new SExp(String.valueOf(n1 + n2));
            }
            else
            {
                throw new Exception("ERROR IN PLUS: Integers only");
            }
        } catch (NumberFormatException e)
        {
            throw new Exception("ERROR IN PLUS: Integers only");
        }
    }

    /**
     * LISP built in subtraction
     * @param s1
     * @param s2
     * @return
     * @throws java.lang.Exception
     */
    public static SExp MINUS(SExp s1, SExp s2)
            throws Exception
    {
        try
        {
            if (s1.IsAtomic() && s2.IsAtomic())
            {
                int n1 = Integer.parseInt(s1.GetValue());
                int n2 = Integer.parseInt(s2.GetValue());
                return new SExp(String.valueOf(n1 - n2));
            }
            else
            {
            throw new Exception("ERROR IN MINUS: Integers only");
            }
        } catch (NumberFormatException e)
        {
            throw new Exception("ERROR IN MINUS: Integers only");
        }
    }

    /**
     * LISP builtin multiplication
     * @param s1
     * @param s2
     * @return
     * @throws java.lang.Exception
     */
    public static SExp TIMES(SExp s1, SExp s2) throws Exception
    {
        try
        {
            if (s1.IsAtomic() && s2.IsAtomic())
            {
                int n1 = Integer.parseInt(s1.GetValue());
                int n2 = Integer.parseInt(s2.GetValue());
                return new SExp(String.valueOf(n1 * n2));
            }
            else
            {
            throw new Exception("ERROR IN TIMES: Integers only");
            }
        } catch (NumberFormatException e)
        {
            throw new Exception("ERROR IN TIMES: Integers only");
        }
    }

    /**
     * LISP builtin division
     * @param s1
     * @param s2
     * @return
     * @throws java.lang.Exception
     */
    public static SExp QUOTIENT(SExp s1, SExp s2) throws Exception
    {
        try
        {
            if (s1.IsAtomic() && s2.IsAtomic())
            {
                int n1 = Integer.parseInt(s1.GetValue());
                int n2 = Integer.parseInt(s2.GetValue());
                return new SExp(String.valueOf(n1 / n2));
            }
            else
            {
                throw new Exception("ERROR IN QUOTIENT: Integers only");
            }
        } catch (ArithmeticException e)
        {
            throw new Exception("ERROR IN QUOTIENT: Division by zero");
        } catch (NumberFormatException e)
        {
            throw new Exception("ERROR IN QUOTIENT: Integers only");
        }
    }

    /**
     * LSIP builtin remainder
     * @param s1
     * @param s2
     * @return
     * @throws java.lang.Exception
     */
    public static SExp REMAINDER(SExp s1, SExp s2) throws Exception
    {
        try
        {
            if (s1.IsAtomic() && s2.IsAtomic())
            {
                int n1 = Integer.parseInt(s1.GetValue());
                int n2 = Integer.parseInt(s2.GetValue());
                return new SExp(String.valueOf(n1 % n2));
            }
            else
            {
                throw new Exception("ERROR IN REMAINDER: Integers only");
            }
        } catch (ArithmeticException e)
        {
            throw new Exception("ERROR IN REMAINDER: Division by zero");
        } catch (NumberFormatException e)
        {
            throw new Exception("ERROR IN REMAINDER: Integers only");
        }
    }

    /**
     * LISP builtin greater than comparison
     * @param s1
     * @param s2
     * @return
     * @throws java.lang.Exception
     */
    public static SExp GREATER(SExp s1, SExp s2) throws Exception
    {
        try
        {
            if (s1.IsAtomic() && s2.IsAtomic())
            {
                int n1 = Integer.parseInt(s1.GetValue());
                int n2 = Integer.parseInt(s2.GetValue());
                if (n1 > n2)
                {
                    return SExp.T;
                } else
                {
                    return SExp.NIL;
                }
            }
            else
            {
                throw new Exception("ERROR IN GREATER: Integers only");
            }
        } catch (NumberFormatException e)
        {
            throw new Exception("ERROR IN GREATER: Integers only");
        }
    }

    /**
     * LISP builtin less than comparison
     * @param s1
     * @param s2
     * @return
     * @throws java.lang.Exception
     */
    public static SExp LESS(SExp s1, SExp s2)
            throws Exception
    {
        try
        {
            if (s1.IsAtomic() && s2.IsAtomic())
            {
                int n1 = Integer.parseInt(s1.GetValue());
                int n2 = Integer.parseInt(s2.GetValue());
                if (n1 < n2)
                {
                    return SExp.T;
                } else
                {
                    return SExp.NIL;
                }
            }
            else
            {
                throw new Exception("ERROR IN LESS: Integers only");
            }
        } catch (NumberFormatException e)
        {
            throw new Exception("ERROR IN LESS: Integers only");
        }
    }

    /**
     * LISP built in equality comparison
     * @param s1
     * @param s2
     * @return
     * @throws java.lang.Exception
     */
    public static SExp EQ(SExp s1, SExp s2)
            throws Exception
    {
        if (!s1.IsAtomic() || !s2.IsAtomic())  //Eq only compares atoms
        {
            throw new Exception("ERROR IN EQ: Atoms only");
        }
        else
        {
            if (s1.GetValue().equalsIgnoreCase(s2.GetValue()))
            {
                return SExp.T;
            } else
            {
                return SExp.NIL;
            }
        }
    }
    
    /**
     * Is the S expression atomic
     * @return true=atomic
     */
    public boolean IsAtomic()
    {
        return isAtomic;
    }

    /**
     * Is the atom an integer
     * @return
     */
    public boolean IsInteger()
    {
        if (!isAtomic)  //if not an atom cant be integer
        {
            return false;
        }
        try
        {
            Integer.parseInt(value);
            return true;
        } catch (NumberFormatException e)  //if no exception then is int
        {
            return false;
        }
    }


    /**
     * Is the atom null
     * @return
     */
    public boolean IsNull()
    {
        if (car == null && cdr == null && value.equalsIgnoreCase("NIL"))  //NIL is null too
        {
            return true;
        }
        return false;
    }

    public static SExp INPUT(InputStream in) throws Exception, IOException
    {
        SExp s;
        String token = NextToken(in);

        if (token.equalsIgnoreCase(""))
        {
            return null;
        }

        if (token.equalsIgnoreCase("("))
        {
            String next = NextToken(in);

            // () is equal to NILL
            if (next.equalsIgnoreCase(")"))
            {
                return SExp.NIL;
            }

            PushedToken(next);

            s = new SExp();
            s.SetCAR(INPUT(in));

            token = NextToken(in);

            if (token.equals("."))
            {
                s.SetCDR(INPUT(in));
                token = NextToken(in);
                //if (# . ) then there is an error since the CDR is missing
                if (!token.equals(")"))
                {
                    throw new Exception("ERROR IN INPUT: Expected ')'");
                }
                else
                {
                    s.SetAtom(false);  //this is obviously not an atom
                    s.SetValue("");   //non atomic has no value
                }
            }
            else  //no . then this must be in list notation
            {
                PushedToken(token);
                s.SetCDR(INPUTList(in));
            }
        }
        else  //no parens so must be atomic
        {
            //no punctuation in atomic
            if (token.equals(")") || token.equals("."))
            {
                throw new Exception("ERROR IN INPUT: '" + token + "' is a bad s expression");
            }
            else
            {
                s = new SExp(token);
            }
        }
        return s;
    }


    /**
     * Read input in list notation
     * @param in
     * @return
     * @throws java.lang.Exception
     * @throws java.io.IOException
     */
    private static SExp INPUTList(InputStream in) throws Exception, IOException
    {
        SExp s = new SExp();
        String token = NextToken(in);

        if (token.equals(")"))
        {
            return SExp.NIL;
        } else
        {
            if (token.equals("."))
            {
                throw new Exception("ERROR IN INPUT: misplaced '.'");
            }
        }
        PushedToken(token);

        s.SetCAR(INPUT(in));

        token = NextToken(in);
        if (token.equals("."))
        {
            s.SetCDR(INPUT(in));
            token = NextToken(in);
            if (!token.equals(")"))
            {
                throw new Exception("ERROR IN INPUT: Ended in middle of s expression");
            }
        } else
        {
            PushedToken(token);
            s.SetCDR(INPUTList(in));
        }
        return s;
    }

    /**
     * Get the length of an S-Expression, primarily used for parameter list
     * checks
     * @return The length of the s expression
     */
    public int Length()
    {
        if (IsNull())
        {
            return 0;
        }

        if (IsAtomic())
        {
            return 1;
        }

        return 1 + cdr.Length();
    }

public static String NextToken(InputStream in) throws Exception
    {
        String token = "";

        if (pushedToken != null)
        {
            token = pushedToken;
            pushedToken = null;
            return token;
        }

        int c = MoveNextRaw(in);

        if (c == -1)
        {
            return "";
        }

        if (c == '(' || c == ')' || c == '.')
        {
            token = String.valueOf((char) c);
        } else
        {
            do
            {
                token += ((char) c);
                in.mark(2);  //on reset comes back here
                c = in.read();
            } while (c != ')' && c != '(' && c != -1 && c != '\n' && c != '\t' && c != ' ');

            if (c == ')' || c == '(' || c == '.')
            {
                in.reset();
            }
        }
        return token;
    }

/**
 * Tokenizer Function to read raw input
 * @param in Inputstream
 * @return The raw values
 * @throws java.lang.Exception
 */
    private static int MoveNextRaw(InputStream in) throws Exception
    {
        int b;
        do
        {
            b = in.read();
        } while (b == ' ' || b == '\t' || b == '\n');  
        //ignore spaces and newlines this lets input span lines, this allows multiline input

        return b;
    }
/**
 * Tokenizer function to get the next valid token
 * @param token The tokenized token
 */
    public static void PushedToken(String token)
    {
        pushedToken = token;
    }
}

