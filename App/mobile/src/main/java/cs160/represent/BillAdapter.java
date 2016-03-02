package cs160.represent;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import org.w3c.dom.Text;

import java.util.List;

/**
 * Created by jianqiao on 2/28/16.
 */
public class BillAdapter extends ArrayAdapter<String> {


    public BillAdapter(Context context, int resource, List<String> bills) {
        super(context, resource, bills);
    }

    @Override
    public View getView(int position, View v, ViewGroup parent) {

        LayoutInflater vi = LayoutInflater.from(getContext());
        v = vi.inflate(R.layout.bill, null);
        TextView bill = (TextView) v.findViewById(R.id.bill);
        bill.setText(getItem(position));
        return v;

    }
}

