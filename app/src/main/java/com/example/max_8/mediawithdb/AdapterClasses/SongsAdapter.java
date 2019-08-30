package com.example.max_8.mediawithdb.AdapterClasses;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.max_8.mediawithdb.Activitys.PlayActivity;
import com.example.max_8.mediawithdb.Model.UploadSongs;
import com.example.max_8.mediawithdb.R;
import com.mikhaellopez.circularimageview.CircularImageView;
import com.squareup.picasso.Picasso;

import java.util.List;



public class SongsAdapter extends RecyclerView.Adapter<SongsAdapter.ViewHolder> {

    Context context;
    List<UploadSongs> aList;

    public SongsAdapter(Context context, List<UploadSongs> aList) {
        this.context = context;
        this.aList = aList;
    }

    @Override
    public SongsAdapter.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {

        View view = LayoutInflater.from(context).inflate(R.layout.row_songs_item,viewGroup,false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(SongsAdapter.ViewHolder viewHolder, int position) {

      UploadSongs uploadSongs = aList.get(position);

      viewHolder.textView_Title.setText(uploadSongs.getSongTitle());
      viewHolder.textView_duration.setText(uploadSongs.getSongDuration());


        Picasso.with(context).load(uploadSongs.getImage()).placeholder(R.drawable.audio).into(viewHolder.circularImageView);
    }

    @Override
    public int getItemCount() { return aList.size(); }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        TextView textView_Title,textView_duration;
        CircularImageView circularImageView ;

        public ViewHolder(View itemView) {
            super(itemView);
            textView_duration = (TextView)itemView.findViewById(R.id.song_duration);
            textView_Title = (TextView)itemView.findViewById(R.id.song_title);
            circularImageView = (CircularImageView) itemView.findViewById(R.id.imageView);
            //here another ways
            itemView.setOnClickListener(this);


        }
        @Override
        public void onClick(View v) {

//            try {
////                ((ShowSongActivity)context).playSong(aList,getAdapterPosition());
//            } catch (IOException e) {
//                e.printStackTrace();
//                Toast.makeText(context,"Error :"+e.getMessage(),Toast.LENGTH_SHORT).show();
//            }

            UploadSongs value = aList.get(getAdapterPosition());


            Intent intent = new Intent(context, PlayActivity.class);
            Bundle bundle = new Bundle();
            bundle.putSerializable("List",value);
            intent.putExtras(bundle);
            intent.putExtra("getAdapter",getAdapterPosition());
            intent.putExtra("title",aList.get(getAdapterPosition()).getSongTitle());
            intent.putExtra("artist",aList.get(getAdapterPosition()).getNameArtist());
            intent.putExtra("images",aList.get(getAdapterPosition()).getImage());
            intent.putExtra("duration",aList.get(getAdapterPosition()).getSongDuration());
            context.startActivity(intent);


        }
    }
}
