import android.content.Context;
import android.widget.ArrayAdapter;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by ashis on 12/22/2016.
 */

public class GridViewAdapter extends ArrayAdapter {

   private ArrayList list = new ArrayList();
    public GridViewAdapter(Context context, int resource,ArrayList list) {
        super(context, resource, list);
        this.list = list;
    }
}
