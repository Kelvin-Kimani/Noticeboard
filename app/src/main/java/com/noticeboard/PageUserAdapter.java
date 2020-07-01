package com.noticeboard;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.ToggleButton;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.amulyakhare.textdrawable.TextDrawable;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;

import de.hdodenhof.circleimageview.CircleImageView;

public class PageUserAdapter extends FirestoreRecyclerAdapter<PageDetails, PageUserAdapter.PageHolder> {

    String pagename, pageinfo, pageID, userID = FirebaseAuth.getInstance().getCurrentUser().getUid(), confirmedPageID;
    private OnItemClickListener listener;
    private OnButtonClickListener buttonClickListener;


    /**
     * Create a new RecyclerView adapter that listens to a Firestore Query.  See {@link
     * FirestoreRecyclerOptions} for configuration options.
     *
     * @param options
     */
    public PageUserAdapter(@NonNull FirestoreRecyclerOptions<PageDetails> options) {
        super(options);
    }

    @Override
    protected void onBindViewHolder(@NonNull PageHolder holder, int position, @NonNull PageDetails model) {

        pagename = model.getPagename();
        pageinfo = model.getPageinfo();
        pageID = model.getPageID();

        holder.textViewPageName.setText(pagename);
        holder.textViewPageInfo.setText(pageinfo);


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
                .buildRect(String.valueOf(firstLetter), Color.BLACK);

        holder.pageImageView.setImageDrawable(drawable);

        // Picasso.get().load(model.getImage(().placeholder(R.drawable.some_drawable).into(ourimageview)


    }

    @NonNull
    @Override
    public PageHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.page_tile_user, parent, false);
        return new PageHolder(v);

    }

    public void setOnItemClickListener(OnItemClickListener listener) {

        this.listener = listener;
    }

    public void setOnButtonClickListener(OnButtonClickListener buttonClickListener) {

        this.buttonClickListener = buttonClickListener;

    }

    public interface OnItemClickListener {

        void onItemClick(DocumentSnapshot documentSnapshot, int position);
    }

    public interface OnButtonClickListener {

        void onButtonClick(DocumentSnapshot documentSnapshot, int position);

    }

    public class PageHolder extends RecyclerView.ViewHolder {

        TextView textViewPageName;
        TextView textViewPageInfo;
        CircleImageView pageImageView;
        ToggleButton followButton;

        public PageHolder(@NonNull View itemView) {
            super(itemView);

            textViewPageName = (itemView).findViewById(R.id.pagename);
            textViewPageInfo = (itemView).findViewById(R.id.bio);
            pageImageView = (itemView).findViewById(R.id.pageprofileimg);
            followButton = (itemView).findViewById(R.id.followbutton);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int position = getAdapterPosition();


                    if (position != RecyclerView.NO_POSITION && listener != null) {

                        listener.onItemClick(getSnapshots().getSnapshot(position), position);

                    }

                }
            });


            followButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // remember to implement
                    int position = getAdapterPosition();

                    if (position != RecyclerView.NO_POSITION && buttonClickListener != null) {

                        buttonClickListener.onButtonClick(getSnapshots().getSnapshot(position), position);

                    }

                }
            });

        }
    }
}
