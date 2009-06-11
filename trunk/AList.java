import java.util.*;

/**
 * LISP Association list
  * @author Keith Johansen
  * johansek@cse.ohio-state.edu
 */
public class AList
{
    LinkedList<SExp> al;

    public AList()
    {
	al=new LinkedList<SExp>();
    }

	public AList(LinkedList<SExp> a)
	{
		al=new LinkedList<SExp>();
		this.al=a;
	}

    /**
     * Get the value of a bound pair
     * @param var The name to search for
     * @return
     */
	public SExp GetBindingPair(String var)
	{
		Iterator itr = al.listIterator();
		SExp binding;

		while (itr.hasNext()){
			binding = (SExp)itr.next();
			if (binding.CAR().GetValue().equalsIgnoreCase(var))
				return binding.CDR();
		}
        //not in alist
		return null;
	}

    /**
     * Add binding pairs to the a list
     * @param aList Current a list
     * @param pars parameter names to add
     * @param values the values to bind
     * @return new a list
     */
	public static AList AddBindingPairs(AList aList, SExp pars, SExp values)
	{
		AList newList = new AList(aList.al);

		SExp pars_t = pars;
		SExp values_t = values;

		while (!pars_t.IsNull() && !values_t.IsNull())	{
			newList.al.addFirst(SExp.CONS(pars_t.CAR(), values_t.CAR()));
			pars_t = pars_t.CDR();
			values_t = values_t.CDR();
		}
		return newList;
	}
}

