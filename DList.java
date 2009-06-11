import java.util.*;

/**
 * LISP definition list
  * @author Keith Johansen
  * johansek@cse.ohio-state.edu
 */
public class DList
{
    private LinkedList<SExp> dl=new LinkedList<SExp>();

	public SExp ADD(SExp def)
	{
		dl.addFirst(SExp.CONS(def.CAR(),SExp.CONS(def.CDR().CAR(), def.CDR().CDR().CAR())));
		return def.CAR();
	}

	public SExp GetFunctionDefinition(String fName)
	{
        if(dl.size()==0)
        {
            return null;
        }
		Iterator itr = dl.listIterator();
		while (itr.hasNext())	{
			SExp def = (SExp)itr.next();
			if (def.CAR().GetValue().equalsIgnoreCase(fName)){
				return def.CDR();
			}
		}
		return null;
	}

	/**
	* Used during debugging only
	*/
	public void Print()
	{
		Iterator itr = dl.listIterator();
		while (itr.hasNext())	{
			System.out.println(((SExp)itr.next()).ToStringDotNotation());
			System.out.println();
		}

	}
}

