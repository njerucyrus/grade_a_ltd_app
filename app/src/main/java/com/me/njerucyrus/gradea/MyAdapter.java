package com.me.njerucyrus.gradea;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

import static android.support.v4.content.ContextCompat.startActivity;

/**
 * Created by njerucyrus on 1/25/18.
 */

public class MyAdapter extends RecyclerView.Adapter<MyAdapter.ViewHolder> {
    private List<RecyclerItem> listItems;
    private Context mContext;

    public MyAdapter(List<RecyclerItem> listItems, Context mContext) {
        this.listItems = listItems;
        this.mContext = mContext;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.recycler_item, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        final RecyclerItem itemList = listItems.get(position);
        String title = "RECEIPT #" + itemList.getReceiptNo();
        String description = "Payee: " + itemList.getPayeeName()  + ", Ksh "+ itemList.getPrice()+".00" +
                "\nDate " + itemList.getDate();
        holder.txtTitle.setText(title);
        holder.txtItemDescription.setText(description);

    }

    @Override
    public int getItemCount() {
        return listItems.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public TextView txtTitle;
        public TextView txtItemDescription;
        public TextView txtOptionDigit;
        public Button btnRecyclerPrintPreview;

        public ViewHolder(View itemView) {
            super(itemView);
            txtTitle = (TextView) itemView.findViewById(R.id.txtTitle);
            txtItemDescription = (TextView) itemView.findViewById(R.id.txtItemDescription);
            btnRecyclerPrintPreview = (Button) itemView.findViewById(R.id.recycler_btn_print_preview);
            btnRecyclerPrintPreview.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    int pos = getAdapterPosition();
                    if (pos != RecyclerView.NO_POSITION){
                        SharedPreferences settings = mContext.getSharedPreferences("PRINT_DATA",
                                Context.MODE_PRIVATE);

                        SharedPreferences.Editor editor = settings.edit();

                        RecyclerItem item = listItems.get(pos);
                        editor.putInt("id", item.getId());
                        editor.apply();
                        editor.commit();

                        view.getContext().startActivity(new Intent(mContext, PrintPreviewActivity.class));
                    }
                }
            });


        }
    }
}
