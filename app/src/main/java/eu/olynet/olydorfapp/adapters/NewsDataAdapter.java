package eu.olynet.olydorfapp.adapters;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.List;

import eu.olynet.olydorfapp.R;
import eu.olynet.olydorfapp.model.NewsItem;
import eu.olynet.olydorfapp.model.Organization;

/**
 * @author <a href="mailto:simon.domke@olynet.eu">Simon Domke</a>
 */
public class NewsDataAdapter extends RecyclerView.Adapter<NewsDataAdapter.ViewHolder> {

    private List<NewsItem> items;
    private Context context;

    public NewsDataAdapter() {

    }

    // Provide a reference to the views for each data item
    // Complex data items may need more than one view per item, and
    // you provide access to all the views for a data item in a view holder
    public static class ViewHolder extends RecyclerView.ViewHolder {
        // each data item is just a string in this case
        protected TextView vDate;
        protected TextView vTitle;
        protected TextView vOrganization;
        protected ImageView vImage;

        public ViewHolder(View v) {
            super(v);
            vOrganization = (TextView) v.findViewById(R.id.newsCardOrganization);
            vDate = (TextView) v.findViewById(R.id.newsCardDate);
            vTitle = (TextView) v.findViewById(R.id.newsCardTitle);
            vImage = (ImageView) v.findViewById(R.id.newsCardImage);
        }
    }

    // Provide a suitable constructor (depends on the kind of dataset)
    public NewsDataAdapter(Context context, List<NewsItem> newsItems) {
        this.context = context;
        this.items = newsItems;
    }

    // Create new views (invoked by the layout manager)
    @Override
    public NewsDataAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // create a new view
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_news, parent, false);
        // set the view's size, margins, paddings and layout parameters
        return new NewsDataAdapter.ViewHolder(v);
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        NewsItem newsItem = items.get(position);

        WindowManager windowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);

        /* Date */
        SimpleDateFormat localFormat = (SimpleDateFormat) android.text.format.DateFormat.getDateFormat(context);
        holder.vDate.setText(localFormat.format(newsItem.getDate()));

        /* Title */
        holder.vTitle.setText(newsItem.getTitle());

        /* Organization */
        Organization organization = Organization.organizations.get(newsItem.getOrganization());
        String orgName;
        if(organization != null) {
            orgName = organization.getName();
        } else {
            orgName = "N/A";
        }
        holder.vOrganization.setText(orgName);

        /* Image */
        byte[] image = newsItem.getImage();
        if(image != null && image.length > 0) {
            Bitmap imageBitmap = BitmapFactory.decodeByteArray(image, 0, image.length);
            DisplayMetrics dm = new DisplayMetrics();
            windowManager.getDefaultDisplay().getMetrics(dm);
            holder.vImage.setImageBitmap(imageBitmap);
        }
    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return items.size();
    }
}
