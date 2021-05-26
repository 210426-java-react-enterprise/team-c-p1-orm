import com.revature.orm.Account;
import com.revature.orm.Customer;
import com.revature.orm.MyObjectRelationalMapper;
import com.revature.orm.Pair;


public class Driver
{
    public static void main(String[] args)
    {
        MyObjectRelationalMapper orm = new MyObjectRelationalMapper(null);
        Pair<String,String> condition = new Pair<>("first_name", "999999999");
        Pair<String,Integer> condition2 = new Pair<>("number", 100);
        Customer customer = orm.readRow(Customer.class, condition);
        Account account = orm.readRow(Account.class, condition2);
    }
}
