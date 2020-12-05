package com.noticeboard.ui.home;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.ToggleButton;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.amulyakhare.textdrawable.TextDrawable;
import com.amulyakhare.textdrawable.util.ColorGenerator;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.noticeboard.PostDetails;
import com.noticeboard.R;

import de.hdodenhof.circleimageview.CircleImageView;

public class HomePostAdapter extends FirestoreRecyclerAdapter<PostDetails, HomePostAdapter.HomePostHolder> {


    final String userID = FirebaseAuth.getInstance().getCurrentUser().getUid();
    String pagename, postTitle, pageID;
    private OnItemClickListener listener;
    private OnSaveItemClickListener saveListener;
    private OnCommentsClickListener commentsListener;
    String status_read = "READ";


    /**
     * Create a new RecyclerView adapter that listens to a Firestore Query.  See {@link
     * FirestoreRecyclerOptions} for configuration options.
     *
     * @param options
     */
    public HomePostAdapter(@NonNull FirestoreRecyclerOptions<PostDetails> options) {
        super(options);
    }

    @Override
    protected void onBindViewHolder(@NonNull HomePostHolder holder, int position, @NonNull PostDetails model) {

        ColorGenerator generator = ColorGenerator.MATERIAL;
        int color = generator.getRandomColor();

        pagename = model.getPagename();
        postTitle = model.getTitle();
        pageID = model.getPageID();
        String saveValue = model.getSaveValue();
        String status = model.getStatus();

        holder.textViewPageName.setText(pagename);
        holder.textViewTitle.setText(postTitle);
        holder.textViewContent.setText(model.getContent());
        //Remember to add time
        holder.textViewTime.setText(model.getTime());

        if (status_read.equals(status)){

            holder.textViewPageName.setTextColor(Color.parseColor("#5C5C5C"));
            holder.textViewTitle.setTextColor(Color.parseColor("#5C5C5C"));
        }
        else{

            holder.textViewPageName.setTextColor(Color.BLACK);
            holder.textViewPageName.setTypeface(Typeface.defaultFromStyle(Typeface.BOLD));

            holder.textViewTitle.setTextColor(Color.BLACK);
            holder.textViewTitle.setTypeface(Typeface.defaultFromStyle(Typeface.BOLD));
        }

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

        holder.savePost.setText(saveValue);
        if ("Yes".equals(saveValue)) {

            holder.savePost.setChecked(true);

        } else {

            holder.savePost.setChecked(false);

        }

    }

    @NonNull
    @Override
    public HomePostHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.post_tile, parent, false);

        return new HomePostHolder(v);
    }

    public void setOnItemClickListener(HomePostAdapter.OnItemClickListener listener) {

        this.listener = listener;

    }

    public void setOnSaveItemClickListener(HomePostAdapter.OnSaveItemClickListener saveListener) {

        this.saveListener = saveListener;

    }

    public void setOnCommentsClickListener(HomePostAdapter.OnCommentsClickListener commentsListener) {
        this.commentsListener = commentsListener;
    }

    public interface OnItemClickListener {
        void onItemClick(DocumentSnapshot documentSnapshot, int position);
    }

    public interface OnSaveItemClickListener {
        void onSaveItemClick(DocumentSnapshot documentSnapshot, int position);
    }

    public interface OnCommentsClickListener {
        void onCommentsClick(DocumentSnapshot documentSnapshot, int position);
    }

    public class HomePostHolder extends RecyclerView.ViewHolder {

        CardView cardView;
        TextView textViewPageName, textViewTitle, textViewContent, textViewTime;
        CircleImageView pageImageView;
        ToggleButton savePost;
        ImageView comments;

        public HomePostHolder(@NonNull View itemView) {
            super(itemView);

            textViewTitle = itemView.findViewById(R.id.posttitle);
            textViewContent = itemView.findViewById(R.id.post);
            textViewPageName = itemView.findViewById(R.id.pagename);
            textViewTime = itemView.findViewById(R.id.time);
            pageImageView = itemView.findViewById(R.id.pageprofileimg);
            cardView = itemView.findViewById(R.id.HomePostCardView);
            savePost = itemView.findViewById(R.id.savebutton);
            comments = itemView.findViewById(R.id.commentbutton);




            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    int position = getAdapterPosition();

                    if (position != RecyclerView.NO_POSITION && listener != null) {

                        listener.onItemClick(getSnapshots().getSnapshot(position), position);

                    }
                }
            });

            savePost.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    int position = getAdapterPosition();

                    if (position != RecyclerView.NO_POSITION && saveListener != null) {

                        saveListener.onSaveItemClick(getSnapshots().getSnapshot(position), position);

                    }
                }
            });

            comments.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    int position = getAdapterPosition();

                    if (position != RecyclerView.NO_POSITION && commentsListener != null) {

                        commentsListener.onCommentsClick(getSnapshots().getSnapshot(position), position);

                    }
                }
            });
        }
    }
}
