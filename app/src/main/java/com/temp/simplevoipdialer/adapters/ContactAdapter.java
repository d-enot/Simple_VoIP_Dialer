package com.temp.simplevoipdialer.adapters;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.provider.ContactsContract;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.temp.simplevoipdialer.CallActivity;
import com.temp.simplevoipdialer.R;
import com.temp.simplevoipdialer.fragments.ContactFragment;
import com.temp.simplevoipdialer.items.Contact;
import com.temp.simplevoipdialer.services.MainService;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * Created by klim-mobile on 03.09.2015.
 */
public class ContactAdapter extends RecyclerView.Adapter<ContactAdapter.ItemViewHolder> implements Filterable{

    private List<Contact> items;
    private List<Contact> filteredItems;

    public ContactAdapter(List<Contact> items) {
        Collections.sort(items, new Comparator<Contact>() {
            @Override
            public int compare(Contact lhs, Contact rhs) {
                return lhs.getName().compareToIgnoreCase(rhs.getName());
            }
        });
        this.items = items;
        this.filteredItems = new ArrayList<>(items);
    }

    @Override
    public ItemViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.contact_row, viewGroup, false);
        ItemViewHolder itemViewHolder = new ItemViewHolder(view);
        return itemViewHolder;
    }

    @Override
    public void onBindViewHolder(ItemViewHolder itemViewHolder, int i) {
        itemViewHolder.name.setText(filteredItems.get(i).getName());
        itemViewHolder.typeNumbet.setText(filteredItems.get(i).getPhoneType());
        itemViewHolder.number.setText(filteredItems.get(i).getNumbers().get(0));
        itemViewHolder.numbers = filteredItems.get(i).getNumbers();
        itemViewHolder.id = filteredItems.get(i).getId();
        itemViewHolder.imageLink = filteredItems.get(i).getImageLink();
        itemViewHolder.setImage();
    }

    @Override
    public int getItemCount() {
        return filteredItems.size();
    }

    @Override
    public Filter getFilter() {
        return new ContactFilter(this, items);
    }

    private static class ContactFilter extends Filter{

        private final ContactAdapter adapter;
        private final List<Contact> originalContacts;
        private final List<Contact> filteredContacts;

        public ContactFilter(ContactAdapter adapter, List<Contact> originalContacts) {
            this.adapter = adapter;
            this.originalContacts = new ArrayList<>(originalContacts);
            this.filteredContacts = new ArrayList<>();
        }

        @Override
        protected FilterResults performFiltering(CharSequence constraint) {

            filteredContacts.clear();
            final FilterResults results = new FilterResults();

            if (constraint.length() == 0) {
                filteredContacts.addAll(originalContacts);
            }else {
                final String filterPattern = constraint.toString().toLowerCase().trim();
                for (Contact contact : originalContacts) {
                    if (contact.getName().toLowerCase().contains(filterPattern)) {
                        filteredContacts.add(contact);
                    }
                }
            }
            results.values = filteredContacts;
            results.count = filteredContacts.size();

            return results;
        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            adapter.filteredItems.clear();
            adapter.filteredItems.addAll((ArrayList<Contact>) results.values);
            adapter.notifyDataSetChanged();
        }
    }

    public class ItemViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        CardView card;
        ImageView contactImage;
        TextView name;
        TextView typeNumbet;
        TextView number;
        ImageButton contactCallButton;
        List<String> numbers;
        String imageLink;
        String id;

        public ItemViewHolder(View itemView) {
            super(itemView);
            itemView.setOnClickListener(this);
            card = (CardView) itemView.findViewById(R.id.contactCard);
            contactImage = (ImageView) itemView.findViewById(R.id.contactImage);
            contactImage.setBackground(null);
            name = (TextView) itemView.findViewById(R.id.contactName);
            typeNumbet = (TextView) itemView.findViewById(R.id.phoneType);
            number = (TextView) itemView.findViewById(R.id.contactNumber);
            contactCallButton = (ImageButton) itemView.findViewById(R.id.contactButtonCall);
            contactCallButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (numbers.size() == 1) {
                        makeCall(number.getText().toString(), imageLink);
                    } else {
                        AlertDialog.Builder builder = new AlertDialog.Builder(ContactFragment.activity);
                        builder.setTitle("Pick a number");
                        builder.setItems(numbers.toArray(new String[numbers.size()]), new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                makeCall(numbers.get(which), imageLink);
                            }
                        });
                        builder.show();
                    }
                }
            });
        }

        void setImage() {
            MainService.setImage(contactImage, imageLink);
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
