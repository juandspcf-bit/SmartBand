package com.misawabus.project.heartRate.fragments;

import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.NumberPicker;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.misawabus.project.heartRate.R;

import com.misawabus.project.heartRate.Utils.DateUtils;

import com.misawabus.project.heartRate.databinding.FragmentPersonalsFieldsBinding;
import com.misawabus.project.heartRate.viewModels.DeviceViewModel;
import com.misawabus.project.heartRate.viewModels.DashBoardViewModel;
import com.misawabus.project.heartRate.Database.entities.Device;
import com.veepoo.protocol.model.enums.ESex;

import java.text.DateFormat;
import java.time.LocalDate;

import java.util.Locale;

public class PersonalFieldsScrollingFragment extends Fragment {
    private FragmentPersonalsFieldsBinding binding;
    private DeviceViewModel deviceViewModel;
    private String macAddress;

    View.OnClickListener insertObserver = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            if (binding.textViewName.getText().equals("")
                    || binding.textViewGender.getText().equals("")
                    || binding.textViewBirthDay.getText().equals("- - -")
                    || binding.textViewWeight.getText().equals("0 kg")
                    || binding.textViewHeight.getText().equals("0 m")
            ) {
                new MaterialAlertDialogBuilder(requireContext())
                        .setTitle("Error")
                        .setMessage("All the fields are mandatory")
                        .setPositiveButton("Accept", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {

                            }
                        })
                        .show();


                return;
            }

            Device device = new Device();
            device.setMacAddress(macAddress);
            device.setName(binding.textViewName.getText().toString());
            device.setGender(binding.textViewGender.getText().toString());
            String dateBirthDay = binding.textViewBirthDay.getText().toString();
            device.setBirthDate(DateUtils.getFormattedDate(dateBirthDay, "-"));
            device.setWeight(binding.textViewWeight.getText().toString());
            device.setHeight(binding.textViewHeight.getText().toString());
            binding.floatingButtonAddUser.setEnabled(false);


            DeviceViewModel.getIsInsertedRowLive().observe(getViewLifecycleOwner(), new Observer<String>() {
                @Override
                public void onChanged(String s) {

                    ESex eSex = device.getGender().equals("Male") ? ESex.MAN : ESex.WOMEN;
                    String Sheight = device.getHeight().substring(0, device.getHeight().indexOf(" m"));
                    double height = Double.parseDouble(Sheight)*100.0;
                    String Sweight = device.getWeight().substring(0, device.getWeight().indexOf(" k"));
                    double weight = Double.parseDouble(Sweight);
                    final String[] split = dateBirthDay.split("-");
                    LocalDate bii = LocalDate.of(Integer.parseInt(split[0]), Integer.parseInt(split[1]), Integer.parseInt(split[2]));
                    int i = LocalDate.now().getMonthValue() - bii.getMonthValue();
                    int offset = i>=0?0:-1;
                    int ageYears = LocalDate.now().getYear()+offset - bii.getYear();
                    Log.d("YEAR", String.valueOf(ageYears));
                    dashBoardViewModel.getHealthsDataManager().synchronizePersonalData(eSex,
                            (int) height,
                            (int) weight,
                            ageYears,
                            10000);

                    new MaterialAlertDialogBuilder(requireContext())
                            .setTitle("Successfully Inserted")
                            .setMessage("Your data has been added")
                            .setPositiveButton("Accept", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {

                                }
                            })
                            .show();
                    binding.floatingButtonAddUser.setEnabled(true);
                }
            });
            DeviceViewModel.insertSingleDeviceRow(device);
        }
    };
    private DashBoardViewModel dashBoardViewModel;


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        deviceViewModel = new ViewModelProvider(requireActivity()).get(DeviceViewModel.class);
        macAddress = deviceViewModel.getMacAddress();
        dashBoardViewModel = new ViewModelProvider(requireActivity()).get(DashBoardViewModel.class);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_personals_fields, container, false);
        binding.setLifecycleOwner(getViewLifecycleOwner());
        return binding.getRoot();
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        DeviceViewModel.getSingleDeviceRow(macAddress).observe(getViewLifecycleOwner(), new Observer<Device>() {
            @Override
            public void onChanged(Device device) {
                if (device == null) {
                    binding.floatingButtonAddUser.setOnClickListener(insertObserver);
                    return;
                }

                binding.textViewName.setText(device.getName());
                binding.textViewGender.setText(device.getGender());
                String birthDay = DateFormat.getDateInstance(DateFormat.SHORT, Locale.JAPAN).format(device.getBirthDate());
                binding.textViewBirthDay.setText(birthDay);
                binding.textViewWeight.setText(device.getWeight());
                binding.textViewHeight.setText(device.getHeight());
                binding.floatingButtonAddUser.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (binding.textViewName.getText().equals("")
                                || binding.textViewGender.getText().equals("")
                                || binding.textViewBirthDay.getText().equals("- - -")
                                || binding.textViewWeight.getText().equals("0 kg")
                                || binding.textViewHeight.getText().equals("0 m")

                        ) {
                            new MaterialAlertDialogBuilder(requireContext())
                                    .setTitle("Error")
                                    .setMessage("All the fields are mandatory")
                                    .setPositiveButton("Accept", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialogInterface, int i) {

                                        }
                                    })
                                    .show();
                            return;
                        }

                        device.setMacAddress(macAddress);
                        device.setName(binding.textViewName.getText().toString());
                        device.setGender(binding.textViewGender.getText().toString());
                        String dateBirthDay = binding.textViewBirthDay.getText().toString();
                        device.setBirthDate(DateUtils.getFormattedDate(dateBirthDay, "-"));
                        device.setWeight(binding.textViewWeight.getText().toString());
                        device.setHeight(binding.textViewHeight.getText().toString());

                        DeviceViewModel.getIsUpdatedRowLive().observe(getViewLifecycleOwner(), new Observer<String>() {
                            @Override
                            public void onChanged(String s) {

                                ESex eSex = device.getGender().equals("Male") ? ESex.MAN : ESex.WOMEN;
                                String Sheight = device.getHeight().substring(0, device.getHeight().indexOf(" m"));
                                double height = Double.parseDouble(Sheight)*100.0;
                                String Sweight = device.getWeight().substring(0, device.getWeight().indexOf(" k"));
                                double weight = Double.parseDouble(Sweight);
                                final String[] split = dateBirthDay.split("-");
                                LocalDate bii = LocalDate.of(Integer.parseInt(split[0]), Integer.parseInt(split[1]), Integer.parseInt(split[2]));
                                int i = LocalDate.now().getMonthValue() - bii.getMonthValue();
                                int offset = i>=0?0:-1;
                                int ageYears = LocalDate.now().getYear()+offset - bii.getYear();

                                dashBoardViewModel.setDevice(device);
                                dashBoardViewModel.getHealthsDataManager().synchronizePersonalData(eSex,
                                        (int) height,
                                        (int) weight,
                                        ageYears,
                                        10000);

                                binding.floatingButtonAddUser.setEnabled(true);
                                new MaterialAlertDialogBuilder(requireContext())
                                        .setTitle("Successfully Updated")
                                        .setMessage("Your data has been updated")
                                        .setPositiveButton("Accept", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialogInterface, int i) {

                                            }
                                        })
                                        .show();

                            }
                        });

                        DeviceViewModel.updateSingleDeviceRow(device);

                    }
                });
            }
        });


        binding.nameButton.setOnClickListener(view1 -> showFragmentNameGender());

        binding.genderButton.setOnClickListener(view12 -> showFragmentNameGender());

        binding.weightButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                BottomSheetDialog dialog = new BottomSheetDialog(requireContext());

                View vista = LayoutInflater.from(getContext()).inflate(R.layout.botton_sheet_weight, null);
                NumberPicker numberPickerWInteger = vista.findViewById(R.id.numberPickerInteger);
                numberPickerWInteger.setMaxValue(150);
                numberPickerWInteger.setMinValue(50);

                NumberPicker numberPickerWDecimals = vista.findViewById(R.id.numberPickerDecimals);
                numberPickerWDecimals.setMaxValue(9);
                numberPickerWDecimals.setMinValue(0);

                String textWeight = binding.textViewWeight.getText().toString();
                if (!textWeight.equals("")) {
                    final String[] textWeightSplit = textWeight.split("\\.");
                    numberPickerWDecimals.setValue(Integer.parseInt(textWeightSplit[1].substring(0, 1)));
                    numberPickerWInteger.setValue(Integer.parseInt(textWeightSplit[0]));
                }


                Button confirmButton = vista.findViewById(R.id.confirmWeightButton);
                confirmButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        int valueI = numberPickerWInteger.getValue();
                        int valueD = numberPickerWDecimals.getValue();
                        binding.textViewWeight.setText(valueI + "." + valueD + " kg");
                        dialog.dismiss();
                    }
                });


                dialog.setCancelable(true);
                dialog.setContentView(vista);
                dialog.show();


            }
        });

        binding.birthDateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                BottomSheetDialog dialog = new BottomSheetDialog(getContext());

                View vista = LayoutInflater.from(getContext()).inflate(R.layout.botton_sheet_date_picker, null);
                DatePicker datePicker = vista.findViewById(R.id.datePicker);


                String textBirthDay = binding.textViewBirthDay.getText().toString();
                if (!textBirthDay.equals("")) {
                    String[] textBirthDaySplit = textBirthDay.split("-");
                    datePicker.init(
                            Integer.parseInt(textBirthDaySplit[0]),
                            Integer.parseInt(textBirthDaySplit[1]) - 1,
                            Integer.parseInt(textBirthDaySplit[2]),
                            null);
                }


                Button confirmButton = vista.findViewById(R.id.confirmDateBornButton);
                confirmButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        int year = datePicker.getYear();
                        int month = datePicker.getMonth();
                        int day = datePicker.getDayOfMonth();

                        binding.textViewBirthDay.setText(String.format("%s-%s-%s", year, ++month, day));

                        dialog.dismiss();
                    }
                });

                dialog.setCancelable(true);
                dialog.setContentView(vista);
                dialog.show();

            }
        });


        binding.heightButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                BottomSheetDialog dialog = new BottomSheetDialog(requireContext());

                View vista = LayoutInflater.from(getContext()).inflate(R.layout.botton_sheet_height, null);
                NumberPicker numberPickerHInteger = vista.findViewById(R.id.numberPickerHInteger);
                numberPickerHInteger.setMaxValue(2);
                numberPickerHInteger.setMinValue(1);

                NumberPicker numberPickerHDecimals = vista.findViewById(R.id.numberPickerHDecimals);
                numberPickerHDecimals.setMaxValue(9);
                numberPickerHDecimals.setMinValue(0);

                String textHeight = binding.textViewHeight.getText().toString();
                if (!textHeight.equals("")) {
                    final String[] textHeightSplit = textHeight.split("\\.");
                    numberPickerHDecimals.setValue(Integer.parseInt(textHeightSplit[1].substring(0, 1)));
                    numberPickerHInteger.setValue(Integer.parseInt(textHeightSplit[0]));
                }


                Button confirmButton = vista.findViewById(R.id.confirmHeightButton);
                confirmButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        int valueI = numberPickerHInteger.getValue();
                        int valueD = numberPickerHDecimals.getValue();
                        binding.textViewHeight.setText(valueI + "." + valueD + " m");
                        dialog.dismiss();
                    }
                });


                dialog.setCancelable(true);
                dialog.setContentView(vista);
                dialog.show();

            }
        });
    }

    private void showFragmentNameGender() {
        BottomSheetDialog dialog = new BottomSheetDialog(requireContext());
        View buttonSheet = LayoutInflater.from(getContext()).inflate(R.layout.botton_sheet_info_user, null);

        String[] optionsGender = {"Male", "Female"};
        ArrayAdapter<String> stringArrayAdapterGender = new ArrayAdapter<>(getContext(), R.layout.list_item, optionsGender);

        AutoCompleteTextView genderTypeMenu = buttonSheet.findViewById(R.id.selectorGender);
        EditText name = buttonSheet.findViewById(R.id.editTextName);

        name.setText(binding.textViewName.getText().toString());
        genderTypeMenu.setAdapter(stringArrayAdapterGender);
        String selectedGender = binding.textViewGender.getText().toString();
        genderTypeMenu.setText(selectedGender, false);

        Button confirmButton = buttonSheet.findViewById(R.id.confirmButton);
        confirmButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                binding.textViewName.setText(name.getText());
                binding.textViewGender.setText(genderTypeMenu.getText().toString());
                dialog.dismiss();
            }
        });


        dialog.setCancelable(true);
        dialog.setContentView(buttonSheet);
        dialog.show();
    }
}