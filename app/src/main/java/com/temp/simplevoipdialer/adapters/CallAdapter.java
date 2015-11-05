package com.temp.simplevoipdialer.adapters;

import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.provider.ContactsContract;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.temp.simplevoipdialer.CallActivity;
import com.temp.simplevoipdialer.R;
import com.temp.simplevoipdialer.items.Call;
import com.temp.simplevoipdialer.services.MainService;

import java.util.List;

/**
 * Created by klim-mobile on 03.09.2015.
 */
public class CallAdapter extends RecyclerView.Adapter<CallAdapter.ItemViewHolder> {

    private List<Call> items;

    public CallAdapter(List<Call> items) {
        this.items = items;
    }

    @Override
    public ItemViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.calls_row, viewGroup, false);
        ItemViewHolder itemViewHolder = new ItemViewHolder(view);
        return itemViewHolder;
    }

    @Override
    public void onBindViewHolder(ItemViewHolder itemViewHolder, int i) {
        String name = items.get(i).getContactName();
        itemViewHolder.name.setText(name);
        String callNumber = items.get(i).getCallNumber();
        itemViewHolder.number.setText(callNumber);
        itemViewHolder.date.setText(items.get(i).getCallDate());
        itemViewHolder.duration.setText(items.get(i).getCallDuration());
        itemViewHolder.id = items.get(i).getId();
        itemViewHolder.imageLink = items.get(i).getImageLink();
        itemViewHolder.setImage();
        itemViewHolder.callType = items.get(i).getCallType();
        if (itemViewHolder.callType != null) {
            itemViewHolder.setImageCallType();
        }
    }

    @Override
    public int getItemCount() {
        return items.size();
    }


    public class ItemViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        CardView card;
        ImageView callImage;
        ImageView callTypeImage;
        TextView name;
        TextView number;
        TextView date;
        TextView duration;
        ImageButton callButton;
        String imageLink;
        String callType;
        String id;
        String callNumber;

        public ItemViewHolder(View itemView) {
            super(itemView);
            itemView.setOnClickListener(this);
            card = (CardView) itemView.findViewById(R.id.callCard);
            callTypeImage = (ImageView) itemView.findViewById(R.id.callTypeImage);
            callImage = (ImageView) itemView.findViewById(R.id.callImageM);
            callImage.setBackground(null);
            name = (TextView) itemView.findViewById(R.id.callName);
            number = (TextView) itemView.findViewById(R.id.callNumber);
            date = (TextView) itemView.findViewById(R.id.callDate);
            duration = (TextView) itemView.findViewById(R.id.durationText);
            callButton = (ImageButton) itemView.findViewById(R.id.callButtonCall);
            callButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    callNumber = number.getText().toString();
                    if (callNumber == null || callNumber.equals("")) {
                        callNumber = name.getText().toString();
                    }
                    makeCall(callNumber, imageLink);
                }
            });
        }

        void setImage() {
            MainService.setImage(callImage, imageLink);
        }

        void setImageCallType() {


            if (callType.equals("OUTGOING")) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    callTypeImage.setBackground(MainService.context.getResources().getDrawable(R.drawable.outgoing_icon, null));
                } else {
                    callTypeImage.setBackground(MainService.context.getResources().getDrawable(R.drawable.outgoing_icon));
                }
            } else if (callType.equals("INCOMING")) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    callTypeImage.setBackground(MainService.context.getResources().getDrawable(R.drawable.incoming_icon, null));
                } else {
                    callTypeImage.setBackground(MainService.context.getResources().getDrawable(R.drawable.incoming_icon));
                }
            } else if (callType.equals("MISSED")) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    callTypeImage.setBackground(MainService.context.getResources().getDrawable(R.drawable.rejected, null));
                } else {
                    callTypeImage.setBackground(MainService.context.getResources().getDrawable(R.drawable.rejected));
                }
            }
        }

        @Override
        public void onClick(View v) {
            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            Uri uri = Uri.withAppendedPath(ContactsContract.Contacts.CONTENT_URI, id);
            intent.setData(uri);
            MainService.context.startActivity(intent);
        }
    }

    private void makeCall(String number, String imageUri) {
        CallActivity.phoneNumber = number;
        CallActivity.imageLink = imageUri;
        Intent intent = new Intent(MainService.context, CallActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        MainService.context.startActivity(intent);
    }
}
