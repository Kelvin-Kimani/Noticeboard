package com.noticeboard;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageSwitcher;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.amulyakhare.textdrawable.TextDrawable;
import com.amulyakhare.textdrawable.util.ColorGenerator;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.firestore.DocumentSnapshot;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;

public class PageProfileAdapter extends FirestoreRecyclerAdapter<PageDetails, PageProfileAdapter.PageHolder> {

    CircleImageView pageImageView;
    String pagename, pageinfo, pageimage;
    private OnItemClickListener listener;

    /**
     * Create a new RecyclerView adapter that listens to a Firestore Query.  See {@link
     * FirestoreRecyclerOptions} for configuration options.
     *
     * @param options
     */
    public PageProfileAdapter(@NonNull FirestoreRecyclerOptions<PageDetails> options) {
        super(options);
    }

    @Override
    protected void onBindViewHolder(@NonNull PageHolder holder, int position, @NonNull PageDetails model) {

        ColorGenerator generator = ColorGenerator.MATERIAL;
        int color = generator.getRandomColor();

        pagename = model.getPagename();
        pageinfo = model.getPageinfo();
        pageimage = model.getPageimage();

        holder.textViewPageName.setText(pagename);
        holder.textViewPageInfo.setText(pageinfo);

        //if (!pageimage.isEmpty()){

            Picasso.get().load(model.getPageimage()).placeholder(R.drawable.user_image_avatar).into(pageImageView);

        //}

        /*else{

            Character firstLetter = pagename.charAt(0);
            TextDrawable drawable = TextDrawable.builder()
                    .beginConfig()
                    .textColor(Color.RED)
                    .fontSize(30)
                    .toUpperCase()
                    .bold()
                    .width(80)  // width in px
                    .height(80) // height in px
                    .endConfig()
                    .buildRect(String.valueOf(firstLetter), color);

            holder.pageImageView.setImageDrawable(drawable);


        }*/


    }


    @NonNull
    @Override
    public PageProfileAdapter.PageHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.page_tile_admin, parent, false);
        return new PageHolder(v);
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }

    public interface OnItemClickListener {

        void onItemClick(DocumentSnapshot documentSnapshot, int position);

    }

    class PageHolder extends RecyclerView.ViewHolder {


        TextView textViewPageName;
        TextView textViewPageInfo;

        public PageHolder(View itemView) {
            super(itemView);

            textViewPageName = (itemView).findViewById(R.id.pagename);
            textViewPageInfo = (itemView).findViewById(R.id.bio);
            pageImageView = (itemView).findViewById(R.id.pageprofileimg);


            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int position = getAdapterPosition();


                    if (position != RecyclerView.NO_POSITION && listener != null) {

                        listener.onItemClick(getSnapshots().getSnapshot(position), position);

                    }

                }
            });
        }
    }
}

