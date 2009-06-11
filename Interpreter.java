import java.io.*;

/**
 * LISP main interpreter
 * @author Keith Johansen
 * johansek@cse.ohio-state.edu
 */
public abstract class Interpreter
{

    private final static String[] LISP_PRIMITIVES =
    {
        "CAR","CDR","CONS","ATOM","EQ","NULL","INT","PLUS","MINUS","TIMES","QUOTIENT","REMAINDER","LESS","GREATER","DEFUN"
    };
    private final static String PROMPT_CHARS = ">>> ";
    private final static String ERROR_CHARS="**ERR** ";

    public static void main(String args[])
    {
        BufferedInputStream standIn = new BufferedInputStream(System.in);

        //init the global a list and d list
        DList dList = new DList();
        AList aList = new AList();

        boolean more = true;
        while (more)  //while there is more input to read
        {
            try
            {
                System.out.print(PROMPT_CHARS);

                SExp input = SExp.INPUT(standIn);
                if (input == null)
                {
                    more = false; //no more to read
                } else
                {
                    SExp output = EVAL(input, aList, dList, true);

                    //all output is in list notation
                    System.out.println(output.ToStringDotNotation());
                }

            } catch (Exception e)
            {
                //all errors are in the form of exceptions that
                //are synthesized up to this level to be printed
                System.out.println(ERROR_CHARS + e.getMessage());
            }
        }
    }

    /**
     *
     * @param s The s expression to evaluate
     * @param aList The current association list
     * @param dList The current definition list
     * @param topLevel True if top level, if not top level no defuns allowed
     * @return The evlauated s expression
     * @throws java.lang.Exception
     */
    public static SExp EVAL(SExp s, AList aList, DList dList, boolean topLevel) throws Exception
    {
        if (s.IsAtomic())
        {
            //if atom is integer or boolean just return
            if (s.IsInteger() || s.GetValue().equalsIgnoreCase("T") || s.GetValue().equalsIgnoreCase("NIL") || s == SExp.NIL || s == SExp.T)
            {
                return s;
            }

            //if the atom is a valid identifier
            if (IsAtomInValidFormat(s.GetValue()))
            {
                SExp binding = aList.GetBindingPair(s.GetValue());
                if (binding != null)
                {
                    return binding;
                } else
                {
                    throw new Exception("ERROR IN EVAL: " + s.GetValue() + " is not bound");
                }
            } else
            {
                throw new Exception("ERROR IN EVAL: '" + s.GetValue() + "' is not a valid identifier");
            }
        } else //non atomic s expressions
        {
            //the car of a function definition must be atomic, that is the name of the function
            if (!s.CAR().IsAtomic())
            {
                throw new Exception("ERROR IN EVAL: '" + s.CAR().toString() + "' is an illegal function name");
            }

            //handle the special forms
            if (s.CAR().GetValue().equalsIgnoreCase("COND"))
            {
                ConditionalValidation(s);
                return EVCON(s.CDR(), aList, dList);

            }

            if (s.CAR().GetValue().equalsIgnoreCase("QUOTE"))
            {
                CheckParamCount("QUOTE", s.CDR(), 1);
                return s.CDR().CAR();

            }

            if (s.CAR().GetValue().equalsIgnoreCase("DEFUN"))
            {
                if (!topLevel)
                {
                    throw new Exception("ERROR IN EVAL: No Nested Defuns allowed");
                }
                DefunValidation(s);
                return dList.ADD(s.CDR());
            }

            String f = s.CAR().GetValue();

            //check to see if the function is a primitive function for special handling
            //built in functions are not in the dlist, just see if exists, apply will apply it
            boolean isLISP_builtin = false;
            for (int i = 0; i < LISP_PRIMITIVES.length; i++)
            {
                if (f.equalsIgnoreCase(LISP_PRIMITIVES[i]))
                {
                    isLISP_builtin = true;
                    break;
                }
            }

            //check if the function is bound on the d list
            //dont need to save the def, apply will read the definition
            if (!isLISP_builtin)
            {
                SExp def = dList.GetFunctionDefinition(f);
                //if null then the function is not on the dlist and is thus not defined
                if (def == null)
                {
                    throw new Exception("ERROR IN EVAL: '" + f + "' is not defined");
                }
            }


            //the parameter list cannot be atomic
            if (s.CDR().IsAtomic())
            {
                throw new Exception("ERROR IN EVAL: '" + s.CAR().GetValue() + "' has bad arguments");
            }


	    
            return APPLY(s.CAR(), EVLIS(s.CDR(), aList, dList), aList, dList);
        }
    }

    /**
     * LISP evaluate list function
     * @param l The list as s expression to evaluate
     * @param aList The current association list
     * @param dList The current definition list
     * @return The evaluated list
     * @throws java.lang.Exception
     */
    public static SExp EVLIS(SExp l, AList aList, DList dList) throws Exception
    {
        if (l.IsNull())
        {
            return SExp.NIL;
        } else
        {
            return SExp.CONS(EVAL(l.CAR(), aList, dList, false), EVLIS(l.CDR(), aList, dList));
        }
    }

    /**
     * LISP evaluate conditional function
     * @param be The condtions, operation pairs
     * @param aList The current association list
     * @param dList The current definition list
     * @return The evaluated conditional
     * @throws java.lang.Exception
     */
    public static SExp EVCON(SExp be, AList aList, DList dList) throws Exception
    {
        if (be.IsNull())
        {
            throw new Exception("ERROR IN EVCON: All conditionals cannot be null");
        }

        //if the current conditional is true, then evaluate its expression
        if (SExp.EQ(EVAL(be.CAR().CAR(), aList, dList, false), SExp.T).GetValue().equals("T"))
        {
            return EVAL(be.CAR().CDR().CAR(), aList, dList, false);
        } else  //get the next conditional in the list
        {
            return EVCON(be.CDR(), aList, dList);
        }


    }

    /**
     * LISP apply function
     * @param f The function to apply
     * @param x the arguments
     * @param aList The current association list
     * @param dList The current definition list
     * @return The applied function return value
     * @throws java.lang.Exception
     */
    public static SExp APPLY(SExp f, SExp x, AList aList, DList dList) throws Exception
    {
        String fName = f.GetValue();

        //Since Java does not allow switching on strings here are a whole lot of ifs


        if (fName.equalsIgnoreCase("CAR"))
        {
            CheckParamCount("CAR", x, 1);
            if (x.CAR().IsAtomic())
            {
                throw new Exception("ERROR IN APPLY: CAR cannot be performed on atom");
            }
            return x.CAR().CAR();
        }


        if (fName.equalsIgnoreCase("CDR"))
        {
            CheckParamCount("CDR", x, 1);
            if (x.CAR().IsAtomic())
            {
                throw new Exception("ERROR IN APPLY: CDR cannot be performed on atom");
            }
            return x.CAR().CDR();
        }


        if (fName.equalsIgnoreCase("CONS"))
        {
            CheckParamCount("CONS", x, 2);
            return SExp.CONS(x.CAR(), x.CDR().CAR());
        }

        if (fName.equalsIgnoreCase("EQ"))
        {
            CheckParamCount("EQ", x, 2);
            return SExp.EQ(x.CAR(), x.CDR().CAR());
        }

        if (fName.equalsIgnoreCase("ATOM"))
        {
            if (x.CAR().IsAtomic())
            {
                return SExp.T;
            } else
            {
                return SExp.NIL;
            }
        }
        if (fName.equalsIgnoreCase("NULL"))
        {
            if (x.CAR().IsNull())
            {
                return SExp.T;
            } else
            {
                return SExp.NIL;
            }
        }

        if (fName.equalsIgnoreCase("INT"))
        {
            CheckParamCount("INT", x, 1);
            if (x.CAR().IsInteger())
            {
                return SExp.T;
            } else
            {
                return SExp.NIL;
            }
        }

        if (fName.equalsIgnoreCase("PLUS"))
        {
            CheckParamCount("PLUS", x, 2);
            return SExp.PLUS(x.CAR(), x.CDR().CAR());
        }

        if (fName.equalsIgnoreCase("MINUS"))
        {
            CheckParamCount("MINUS", x, 2);
            return SExp.MINUS(x.CAR(), x.CDR().CAR());
        }

        if (fName.equalsIgnoreCase("TIMES"))
        {
            CheckParamCount("TIMES", x, 2);
            return SExp.TIMES(x.CAR(), x.CDR().CAR());
        }

        if (fName.equalsIgnoreCase("QUOTIENT"))
        {
            CheckParamCount("QUOTIENT", x, 2);
            return SExp.QUOTIENT(x.CAR(), x.CDR().CAR());
        }

        if (fName.equalsIgnoreCase("REMAINDER"))
        {
            CheckParamCount("REMAINDER", x, 2);
            return SExp.REMAINDER(x.CAR(), x.CDR().CAR());
        }

        if (fName.equalsIgnoreCase("LESS"))
        {
            CheckParamCount("LESS", x, 2);
            return SExp.LESS(x.CAR(), x.CDR().CAR());
        }

        if (fName.equalsIgnoreCase("GREATER"))
        {
            CheckParamCount("GREATER", x, 2);
            return SExp.GREATER(x.CAR(), x.CDR().CAR());
        }

        //Defualt case means that the function is not built in
        SExp def = dList.GetFunctionDefinition(fName);

        SExp pars = def.CAR();
        SExp body = def.CDR();


        CheckParamCount(fName, x, pars.Length());

        //evaluate, after adding bindings to the assoc list for the parameters
        return EVAL(body, AList.AddBindingPairs(aList, pars, x), dList, false);

    }

    /**
     * Make sure the parameter list is of the expected length
     * @param funcName The function, just for error printing
     * @param pList The parameter list
     * @param num The expected number of parameters
     * @throws java.lang.Exception
     */
    public static void CheckParamCount(String funcName, SExp paramList, int num) throws Exception
    {
        if (paramList.Length() != num)
        {
            throw new Exception(funcName + " expects " + num + " parameters, but " + paramList.Length() + " were provided");
        }
    }

    /**
     * Helper function to validate conditionals,part of eval
     * @param condExp
     * @throws java.lang.Exception
     */
    public static void ConditionalValidation(SExp condExp) throws Exception
    {

        SExp expList = condExp.CDR();

        if (expList.IsAtomic())
        {
            throw new Exception("ERROR IN EVAL: conditonal cannot be atomic");
        }

        int len = expList.Length();

        for (int i = 0; i < len; i++)
        {
            SExp cond = expList.CAR();
            if (cond.Length() != 2)  //condit and expression
            {
                throw new Exception("ERROR IN EVAL: condtional is not in good form");
            }
            expList = expList.CDR();
        }

    }

    /**
     * Helper function to validate custom defuns, part of eval
     * @param def
     * @throws java.lang.Exception
     */
    public static void DefunValidation(SExp def) throws Exception
    {
        if (def.Length() != 4)
        {
            throw new Exception("ERROR IN EVAL: function definiton is not in good form");
        }

        SExp fName = def.CDR().CAR();

        if (!IsIdentifierInValidFormat(fName.GetValue()) || !fName.IsAtomic())
        {
            throw new Exception("ERROR IN EVAL: function name is bad");
        }

        SExp parameterList = def.CDR().CDR().CAR();

        if (parameterList.IsAtomic())
        {
            throw new Exception("ERROR IN EVAL: parameter list is bad");
        }

        int len = parameterList.Length();

        for (int i = 0; i < len; i++)
        {
            SExp par = parameterList.CAR();
            if (!par.IsAtomic() || !IsIdentifierInValidFormat(par.GetValue()))
            {
                throw new Exception("ERROR IN EVAL: '" + par.ToStringListNotation() + "' is an bad parameter");
            }
            parameterList = parameterList.CDR();
        }
    }

    /**
     * Helper function to check if the atom is valid
     * @param ID
     * @return
     */
    public static boolean IsAtomInValidFormat(String ID)
    {
        try
        {
            Integer.parseInt(ID);
            return true;
        } catch (Exception e)
        {
            if (IsIdentifierInValidFormat(ID))
            {
                return true;
            } else
            {
                return false;
            }
        }
    }

    /**
     * Helper function to check if the identifier is in a valid form
     * @param ID
     * @return
     */
    public static boolean IsIdentifierInValidFormat(String ID)
    {
        //first char must be a letter
        if (!Character.isLetter(ID.charAt(0)))
        {
            return false;
        }
        //subsequent characters can be letters or digits, but no punctuation
        for (int i = 1; i < ID.length(); i++)
        {
            if (!Character.isLetterOrDigit(ID.charAt(i)))
            {
                return false;
            }
        }
        return true;
    }
}

