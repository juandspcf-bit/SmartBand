package com.misawabus.project.heartRate.fragments.summaryFragments;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.core.view.WindowCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.core.view.WindowInsetsControllerCompat;
import androidx.fragment.app.Fragment;

import com.google.android.material.datepicker.CalendarConstraints;
import com.google.android.material.datepicker.DateValidatorPointBackward;
import com.google.android.material.datepicker.MaterialDatePicker;
import com.misawabus.project.heartRate.DashBoardActivity;
import com.misawabus.project.heartRate.R;
import com.misawabus.project.heartRate.Utils.DateUtils;
import com.misawabus.project.heartRate.viewModels.DeviceViewModel;
import com.misawabus.project.heartRate.fragments.MainDashBoardFragment;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.DateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.function.Consumer;

public class SummaryFragment extends Fragment {
    protected DeviceViewModel deviceViewModel;
    Date currentDate;

    public SummaryFragment() {
        super();
    }

    protected void backMainFragment() {
        getParentFragmentManager().popBackStack();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        WindowInsetsControllerCompat windowInsetsController2 =
                WindowCompat.getInsetsController(getActivity().getWindow(), getActivity().getWindow().getDecorView());
        windowInsetsController2.setAppearanceLightStatusBars(true);
        getActivity().getWindow().setStatusBarColor(ContextCompat.getColor(getContext(), R.color.white));

    }

    protected void shareScreen(){
        View viewToShare = getActivity().getWindow().getDecorView().getRootView();

        String filename = "screen.jpg";

        viewToShare.setDrawingCacheEnabled(true);
        Bitmap bitmap = Bitmap.createBitmap(viewToShare.getDrawingCache());
        viewToShare.setDrawingCacheEnabled(false);

        File fileImage = new File(getContext().getExternalFilesDir(null), filename);

        try (FileOutputStream outputStream = new FileOutputStream(fileImage)) {
            bitmap.compress(Bitmap.CompressFormat.JPEG, 80, outputStream);
            outputStream.flush();

            Uri apkURI = FileProvider.getUriForFile(getContext(), getContext().getPackageName() + ".provider", fileImage);
            Intent intent = new Intent(Intent.ACTION_SEND);
            intent.setType("*/*");
            intent.putExtra(Intent.EXTRA_SUBJECT, "data");
            intent.putExtra(Intent.EXTRA_STREAM, apkURI);
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            getContext().startActivity(intent);


        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    protected void getDateFromCalendar(Consumer<Date> consumer) {
        CalendarConstraints.Builder builder = new CalendarConstraints.Builder()
                .setValidator(DateValidatorPointBackward.now());

        MaterialDatePicker.Builder<Long> select_date = MaterialDatePicker.Builder.datePicker()
                .setTitleText("Select date");
        MaterialDatePicker<Long> datePicker;
        if(currentDate==null) {
            datePicker = select_date
                    .setCalendarConstraints(builder.build())
                    .build();
        } else{
            datePicker = select_date
                    .setSelection(currentDate.getTime())
                    .setCalendarConstraints(builder.build())
                    .build();
        }

        datePicker.addOnPositiveButtonClickListener(selection -> {
            Date date = new Date(selection);
            currentDate = date;
            String dateS = DateFormat.getDateInstance(DateFormat.SHORT, Locale.JAPAN).format(date);
            Date formattedDate = DateUtils.getFormattedDate(dateS, "/");
            consumer.accept(formattedDate);

        });




        datePicker.show(getChildFragmentManager(), "tag");




    }

    protected void setTextButtonDate(Date date, Button buttonDate) {
        String dateS = DateFormat
                .getDateInstance(DateFormat.SHORT, Locale.JAPAN)
                .format(date);
        buttonDate.setText(DateUtils.getStringFormattedDate(dateS, "/"));
    }

    public static class ContainerDouble {

        private final Double value;
        private final int index;

        public ContainerDouble(Double value, int index) {
            this.value = value;
            this.index = index;
        }

        public Double getValue() {
            return value;
        }

        public int getIndex() {
            return index;
        }
    }


    @Override
    public void onResume() {
        super.onResume();



    }
}
