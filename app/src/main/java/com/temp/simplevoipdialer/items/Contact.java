package com.temp.simplevoipdialer.items;

import android.graphics.Bitmap;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by klim-mobile on 03.09.2015.
 */
public class Contact{

    private Bitmap contactImage;
    private String id;
    private String name;
    private String phoneType;
    private String imageLink;
    private List<String> numbers;

    public Bitmap getContactImage() {
        return contactImage;
    }

    public void setContactImage(Bitmap contactImage) {
        this.contactImage = contactImage;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<String> getNumbers() {
        return numbers;
    }

    public void setPhoneNumbers(String number) {
        if (numbers == null) {
            numbers = new ArrayList<>();
        }
        numbers.add(number);
    }

    public String getPhoneType() {
        return phoneType;
    }

    public void setPhoneType(String phoneType) {
        this.phoneType = phoneType;
    }

    public String getImageLink() {
        return imageLink;
    }

    public void setImageLink(String imageLink) {
        this.imageLink = imageLink;
    }

    public void cleanNumbers() {
        if (!(numbers == null)) {
            numbers.clear();
        }
    }

    @Override
    public boolean equals(Object o) {
        for (String phone : numbers) {
            for (String phoneO : ((Contact) o).getNumbers()) {
                if (phone.equals(phoneO)) {
                    return true;
                } else if(phone.length()<phoneO.length()&&(phoneO.length()-phone.length()<5)) {
                    boolean equal = false;
                    for (int i = 1; i < 5; ) {
                        equal = phoneO.substring(i).equals(phone);
                        if (!equal) {
                            i++;
                        }else return true;
                    }
                }
            }
        }
        if (name.equals(((Contact) o).getName())) {
            return true;
        }
        if (id.equals(((Contact) o).getId())) {
            return true;
        }

        return false;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;

        result = prime * result + ((name == null) ? 0 : name.hashCode());
        result = prime * result + ((id == null) ? 0 : id.hashCode());
        for (String phone : numbers) {
            result = prime * result + phone.hashCode();
        }

        return result;

    }
}
