package com.example.privateleassonapp;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.util.AttributeSet;
import android.widget.ArrayAdapter;

import java.util.ArrayList;
import java.util.Arrays;

public class MultiSpinner extends androidx.appcompat.widget.AppCompatSpinner implements
        DialogInterface.OnMultiChoiceClickListener, DialogInterface.OnCancelListener {

    private ArrayList<String> items;
    private boolean[] selected;
    private String defaultText;
    private MultiSpinnerListener listener;
    public String spinnerText = "...";
    public String addText ="";
    public  AlertDialog.Builder builder;

    public static final String NOT_CHOSE_CATEGORY = "~Choose Category~";
    public static ArrayList<String> getClassCategoryList() {
        ArrayList<String> list = new ArrayList<>();
        list.add("Math");
        list.add("Physics");
        list.add("English");
        list.add("Computer Science");
        list.add("History");
        return list;
    }

    public MultiSpinner(Context context) {
        super(context);
    }

    public MultiSpinner(Context arg0, AttributeSet arg1) {
        super(arg0, arg1);
    }

    public MultiSpinner(Context arg0, AttributeSet arg1, int arg2) {
        super(arg0, arg1, arg2);
    }

    @Override
    public void onClick(DialogInterface dialog, int which, boolean isChecked) {
        selected[which] = isChecked;
    }

    @Override
    public void onCancel(DialogInterface dialog) {
        // refresh text on spinner
        StringBuilder spinnerBuffer = new StringBuilder();
        boolean someSelected = false;
        for (int i = 0; i < items.size(); i++) {
            if (selected[i]) {
                spinnerBuffer.append(items.get(i).trim());
                spinnerBuffer.append(", ");
                spinnerBuffer.append(addText);
                someSelected = true;
            }
        }
        if (someSelected) {
            spinnerText = spinnerBuffer.toString();
            if (spinnerText.length() > 2)
                spinnerText = spinnerText.substring(0, spinnerText.length() - 2); //spinnerText = spinnerText.substring(0, spinnerText.length() - 2);
        } else {
            spinnerText = defaultText;
        }
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getContext(),
                android.R.layout.simple_spinner_item,
                new String[] { spinnerText });
        setAdapter(adapter);
        listener.onItemsSelected(selected);
    }

    @Override
    public boolean performClick() {
        builder = new AlertDialog.Builder(getContext());
        builder.setMultiChoiceItems(items.toArray(new CharSequence[items.size()]), selected, this);
        builder.setPositiveButton(android.R.string.ok,
                (dialog, which) -> {
                    dialog.cancel();
                    //dialog.dismiss();
                });
        builder.setOnCancelListener(this);
        builder.show();
        return true;
    }

    public void setItems(ArrayList<String> items, String allText,
                         MultiSpinnerListener listener) {
        this.items = items;
        this.defaultText = allText;
        this.listener = listener;

        // all didn't selected by default
        selected = new boolean[items.size()];
        Arrays.fill(selected, false);

        // all text on the spinner
        ArrayAdapter<String> adapter = new ArrayAdapter<>(getContext(),
                android.R.layout.simple_spinner_item, new String[]{allText});
        setAdapter(adapter);
    }

    public interface MultiSpinnerListener {
        void onItemsSelected(boolean[] selected);
    }

    public String getSpinnerText() { return spinnerText; }

    public void setSelected(String list)
    {
        // must be *after* setItems
        selected = new boolean[items.size()];
        for (int i = 0; i < selected.length; i++)
        {
            selected[i] = list.contains(items.get(i));
        }

        StringBuilder spinnerBuffer = new StringBuilder();
        boolean someSelected = false;
        for (int i = 0; i < items.size(); i++) {
            if (selected[i] == true) {
                String item = items.get(i).trim();
                spinnerBuffer.append(item);
                spinnerBuffer.append(", ");
                someSelected = true;
            }
        }
        if (someSelected) {
            spinnerText = spinnerBuffer.toString().trim();
            if (spinnerText.length() > 2)
                spinnerText = spinnerText.substring(0, spinnerText.length() - 1); // and not -2
        } else {
            spinnerText = defaultText;
        }
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getContext(),
                android.R.layout.simple_spinner_item,
                new String[] { spinnerText });
        setAdapter(adapter);
        listener.onItemsSelected(selected);
    }
}