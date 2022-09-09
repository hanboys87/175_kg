package com.han.a175_kg;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

import androidx.recyclerview.widget.RecyclerView;

public class ListAdapter extends RecyclerView.Adapter<ListAdapter.ViewHolder> {

    private Context mContext;
    private Listener mListener;

    public interface Listener{
        void onItemSelected(int position);
    }

    ListAdapter(ArrayList<String> list, Listener listener) {
        mDataKg = list ;
        mListener = listener;
    }

    ListAdapter(Context context , ArrayList<String> list,ArrayList<String> time, Listener listener) {
        mDataKg = list ;
        mDataTime=time;
        mContext = context;
        mListener = listener;
    }

    private ArrayList<String> mDataKg = null ;
    private ArrayList<String> mDataTime = null ;
    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        TextView time ;
        TextView kg ;
        TextView tv_send ;


        ViewHolder(View itemView) {
            super(itemView) ;
            time = itemView.findViewById(R.id.tv_recycle_phone) ;
            kg = itemView.findViewById(R.id.tv_recycle_filter) ;

            //Log.e("HAN",tv_phone.getText().toString());
            //tv_filter.setOnClickListener(this);

        }


        @Override
        public void onClick(View v) {
            int mSelectedIndex = getLayoutPosition();
            Toast.makeText(mContext,"position"+mSelectedIndex,Toast.LENGTH_SHORT).show();
            if (mListener != null) {
                mListener.onItemSelected(mSelectedIndex);
            }

        }
    }

    @Override
    public ListAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext() ;
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) ;

        View view = inflater.inflate(R.layout.recycleview_item, parent, false) ;
        ListAdapter.ViewHolder vh = new ListAdapter.ViewHolder(view) ;

        return vh ;
    }

    // onBindViewHolder() - position에 해당하는 데이터를 뷰홀더의 아이템뷰에 표시.
    @Override
    public void onBindViewHolder(ListAdapter.ViewHolder holder, int position) {
        String text = mDataKg.get(position) ;
        holder.time.setText(mDataTime.get(position)) ;
        holder.kg.setText(mDataKg.get(position)) ;
        //holder.tv_filter.setText(DataBase.getInstance(mContext).getFilter(position)) ;
        //holder.tv_send.setText(DataBase.getInstance(mContext).get_Send_SMS(position)) ;
    }

    // getItemCount() - 전체 데이터 갯수 리턴.
    @Override
    public int getItemCount() {
        return mDataKg.size() ;
    }

}