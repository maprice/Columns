package com.example.mprice.mpcolumn.activities;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.Spinner;

import com.example.mprice.mpcolumn.R;
import com.example.mprice.mpcolumn.models.SortModel;

import org.parceler.Parcels;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by mprice on 2/12/16.
 */
public class SortDialogFragment extends DialogFragment {

    @Bind(R.id.dpBeginDate)
    DatePicker dpBeginDate;

    @Bind(R.id.cbArts)
    CheckBox cbArts;

    @Bind(R.id.cbSports)
    CheckBox cbSports;

    @Bind(R.id.cbScience)
    CheckBox cbScience;

    @Bind(R.id.cbTechnology)
    CheckBox cbTechnology;

    @Bind(R.id.cbWorld)
    CheckBox cbWorld;

    @Bind(R.id.sSortOrder)
    Spinner sSortOrder;

    @Bind(R.id.btnCancel)
    Button btnCancel;

    @Bind(R.id.btnSave)
    Button btnSave;

    @Bind(R.id.btnReset)
    Button btnReset;

    private static final String ARG_SORT_MODEL = "sortModel";
    private SortModel mSortModel;
    private OnFragmentInteractionListener mListener;

    public interface OnFragmentInteractionListener {
        void onSaveSelected(SortModel sortModel);
        void onResetSelected();
    }

    public static SortDialogFragment newInstance(SortModel mSortModel) {
        SortDialogFragment fragment = new SortDialogFragment();
        Bundle args = new Bundle();

        args.putParcelable(ARG_SORT_MODEL, Parcels.wrap(mSortModel));
        fragment.setArguments(args);

        return fragment;
    }

    public SortDialogFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
             mSortModel = Parcels.unwrap(getArguments().getParcelable(ARG_SORT_MODEL));
        }
    }

    private void configureView() {
        dpBeginDate.updateDate(mSortModel.beginDateYear, mSortModel.beginDateMonth, mSortModel.beginDateDay);

        sSortOrder.setAdapter(new ArrayAdapter<>(getContext(), android.R.layout.simple_spinner_item, SortModel.SortOrder.values()));
        sSortOrder.setSelection(mSortModel.order.ordinal());
        cbArts.setChecked(mSortModel.newDeskArts);
        cbSports.setChecked(mSortModel.newDeskSports);
        cbScience.setChecked(mSortModel.newDeskScience);
        cbTechnology.setChecked(mSortModel.newDeskTechnology);
        cbWorld.setChecked(mSortModel.newDeskWorld);

        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mSortModel.newDeskSports = cbSports.isChecked();
                mSortModel.newDeskArts = cbArts.isChecked();
                mSortModel.newDeskScience = cbScience.isChecked();
                mSortModel.newDeskTechnology = cbTechnology.isChecked();
                mSortModel.newDeskWorld = cbWorld.isChecked();

                mSortModel.order = (SortModel.SortOrder) sSortOrder.getSelectedItem();
                mSortModel.beginDateDay = dpBeginDate.getDayOfMonth();
                mSortModel.beginDateMonth = dpBeginDate.getMonth();
                mSortModel.beginDateYear = dpBeginDate.getYear();

                if (mListener != null) {
                    mListener.onSaveSelected(mSortModel);
                }

                getDialog().dismiss();
            }
        });

        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getDialog().dismiss();
            }
        });

        btnReset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mListener != null) {
                    mListener.onResetSelected();
                }

                getDialog().dismiss();
            }
        });
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_sort_dialog, container, false);
        ButterKnife.bind(this, view);
        getDialog().getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        this.configureView();

        return view;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mListener = (OnFragmentInteractionListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }
}
