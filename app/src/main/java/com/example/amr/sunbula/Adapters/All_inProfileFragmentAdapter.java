package com.example.amr.sunbula.Adapters;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.amr.sunbula.Activities.DetailsCauseActivity;
import com.example.amr.sunbula.Activities.EditCauseActivity;
import com.example.amr.sunbula.Models.APIResponses.CompleteOrDeleteCauseResponse;
import com.example.amr.sunbula.Models.DBFlowWrappers.AllCausesProfileWrapper;
import com.example.amr.sunbula.R;
import com.example.amr.sunbula.RetrofitAPIs.APIService;
import com.example.amr.sunbula.RetrofitAPIs.ApiUtils;
import com.google.gson.Gson;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class All_inProfileFragmentAdapter extends ArrayAdapter<AllCausesProfileWrapper> {

    private Context activity;
    private List<AllCausesProfileWrapper> list_name_cause;
    private APIService mAPIService;
    private ProgressDialog pdialog;

    public All_inProfileFragmentAdapter(Context context, List<AllCausesProfileWrapper> list_name_cause) {
        super(context, R.layout.item_in_bottom_profile);
        this.activity = context;
        this.list_name_cause = list_name_cause;

        pdialog = new ProgressDialog(activity);
        pdialog.setIndeterminate(true);
        pdialog.setCancelable(false);
        pdialog.setMessage("Loading. Please wait...");

        mAPIService = ApiUtils.getAPIService();
    }

    @Override
    public int getCount() {
        return list_name_cause.size();
    }

    @Override
    public AllCausesProfileWrapper getItem(int position) {
        return list_name_cause.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        final ViewHolderNotifications holder;

        LayoutInflater inflater = (LayoutInflater) activity.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.item_in_bottom_profile, parent, false);
            holder = new ViewHolderNotifications(convertView);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolderNotifications) convertView.getTag();
        }

        holder.text_name_cause.setText(list_name_cause.get(position).getCaseName());
        holder.text_details_cause.setText(list_name_cause.get(position).getCaseDescription());

        if (list_name_cause.get(position).getStatus() == 2)
            holder.text_Completed.setText("Completed");
        else
            holder.text_Completed.setText("");

        holder.image_edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(activity, EditCauseActivity.class);
                Bundle b = new Bundle();
                b.putString("CauseID", list_name_cause.get(position).getCauseID());
                b.putString("Name", list_name_cause.get(position).getCaseName());
                b.putInt("Amount", list_name_cause.get(position).getAmount());
                b.putString("EndDate", list_name_cause.get(position).getEndDate());
                b.putString("CauseDescription", list_name_cause.get(position).getCaseDescription());
                b.putString("Image", list_name_cause.get(position).getIMG());
                i.putExtras(b);
                ((Activity) activity).startActivity(i);
            }
        });

        holder.image_delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(activity);
                builder.setMessage("Do you want to delete " + list_name_cause.get(position).getCaseName() + " ?")
                        .setPositiveButton("yes", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {

                                CompleteOrDeletePost(list_name_cause.get(position).getCauseID(), 1, position);

                            }
                        }).setNegativeButton("no", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // Nothing
                    }
                });
                AlertDialog d = builder.create();
                d.setTitle("Are you sure");
                d.show();
            }
        });

        holder.image_close1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(activity);
                builder.setMessage("Do you want to close " + list_name_cause.get(position).getCaseName() + " ?")
                        .setPositiveButton("yes", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {

                                CompleteOrDeletePost(list_name_cause.get(position).getCauseID(), 2, position);
                                if (list_name_cause.get(position).isSelected()) {
                                    holder.text_details_cause.setVisibility(View.VISIBLE);
                                    holder.text_Completed.setText("Completed");
                                    holder.text_Completed.setVisibility(View.VISIBLE);
                                    holder.image_edit.setVisibility(View.GONE);
                                    holder.image_close1.setVisibility(View.GONE);
                                    holder.image_delete.setVisibility(View.GONE);
                                    holder.image_switch.setVisibility(View.GONE);
                                    holder.image_switch2.setVisibility(View.VISIBLE);

                                } else {
                                    holder.text_details_cause.setVisibility(View.VISIBLE);
                                    holder.text_Completed.setVisibility(View.GONE);
                                    holder.image_edit.setVisibility(View.VISIBLE);
                                    holder.image_close1.setVisibility(View.VISIBLE);
                                    holder.image_delete.setVisibility(View.VISIBLE);
                                    holder.image_switch.setVisibility(View.GONE);
                                    holder.image_switch2.setVisibility(View.VISIBLE);
                                }

                            }
                        }).setNegativeButton("no", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // Nothing
                    }
                });
                AlertDialog d = builder.create();
                d.setTitle("Are you sure");
                d.show();
            }
        });

        if (list_name_cause.get(position).isSelected()) {
            holder.text_details_cause.setVisibility(View.VISIBLE);
            holder.text_Completed.setVisibility(View.VISIBLE);
            if (!list_name_cause.get(position).isJoined()) {
                if (list_name_cause.get(position).getStatus() != 2) {
                    holder.image_edit.setVisibility(View.VISIBLE);
                    holder.image_close1.setVisibility(View.VISIBLE);
                    holder.image_delete.setVisibility(View.VISIBLE);
                }
            }
            holder.image_switch.setVisibility(View.GONE);
            holder.image_switch2.setVisibility(View.VISIBLE);

        } else {
            holder.text_details_cause.setVisibility(View.GONE);
            holder.text_Completed.setVisibility(View.GONE);
            holder.image_edit.setVisibility(View.GONE);
            holder.image_close1.setVisibility(View.GONE);
            holder.image_delete.setVisibility(View.GONE);
            holder.image_switch.setVisibility(View.VISIBLE);
            holder.image_switch2.setVisibility(View.GONE);
        }

        holder.image_switch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                list_name_cause.get(position).setSelected(true);

                holder.text_details_cause.setVisibility(View.VISIBLE);
                holder.text_Completed.setVisibility(View.VISIBLE);
                if (!list_name_cause.get(position).isJoined()) {
                    if (list_name_cause.get(position).getStatus() != 2) {
                        holder.image_edit.setVisibility(View.VISIBLE);
                        holder.image_close1.setVisibility(View.VISIBLE);
                        holder.image_delete.setVisibility(View.VISIBLE);
                    }
                }
                holder.image_switch.setVisibility(View.GONE);
                holder.image_switch2.setVisibility(View.VISIBLE);

            }
        });
        holder.image_switch2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                list_name_cause.get(position).setSelected(false);

                holder.text_details_cause.setVisibility(View.GONE);
                holder.text_Completed.setVisibility(View.GONE);
                holder.image_edit.setVisibility(View.GONE);
                holder.image_close1.setVisibility(View.GONE);
                holder.image_delete.setVisibility(View.GONE);
                holder.image_switch.setVisibility(View.VISIBLE);
                holder.image_switch2.setVisibility(View.GONE);

            }
        });

        holder.text_name_cause.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent i = new Intent(activity, DetailsCauseActivity.class);
                Gson gson = new Gson();
                String myJson = gson.toJson(list_name_cause.get(position));
                Bundle b = new Bundle();
                b.putString("myObject", myJson);
                b.putInt("id", 2);
                i.putExtras(b);
                activity.startActivity(i);

            }
        });

        return convertView;
    }

    private void CompleteOrDeletePost(String CauseID, final int ActionType, final int position) {
        pdialog.show();
        mAPIService.CompleteOrDelete(CauseID, ActionType).enqueue(new Callback<CompleteOrDeleteCauseResponse>() {

            @Override
            public void onResponse(Call<CompleteOrDeleteCauseResponse> call, Response<CompleteOrDeleteCauseResponse> response) {

                if (response.isSuccessful()) {
                    if (response.body().isIsSuccess()) {
                        if (ActionType == 1) {
                            Toast.makeText(activity, "Deleted Successfully", Toast.LENGTH_SHORT).show();
//                            list_name_cause.remove(position);
//                            All_inProfileFragmentAdapter.this.notifyDataSetChanged();
                        } else {
                            Toast.makeText(activity, "Completed cause Successfully", Toast.LENGTH_SHORT).show();
                            list_name_cause.get(position).setSelected(true);
                        }
                    } else
                        Toast.makeText(activity, response.body().getErrorMessage(), Toast.LENGTH_SHORT).show();
                }
                pdialog.dismiss();
            }

            @Override
            public void onFailure(Call<CompleteOrDeleteCauseResponse> call, Throwable t) {
                pdialog.dismiss();
            }
        });
    }

    private class ViewHolderNotifications {
        private TextView text_name_cause, text_details_cause, text_Completed;
        private ImageView image_switch, image_switch2, image_edit, image_close1, image_delete;

        private ViewHolderNotifications(View v) {
            text_name_cause = (TextView) v.findViewById(R.id.text_name_cause);
            text_details_cause = (TextView) v.findViewById(R.id.text_details_cause);
            text_Completed = (TextView) v.findViewById(R.id.text_Completed);
            image_switch = (ImageView) v.findViewById(R.id.image_switch);
            image_switch2 = (ImageView) v.findViewById(R.id.image_switch2);
            image_edit = (ImageView) v.findViewById(R.id.image_edit);
            image_close1 = (ImageView) v.findViewById(R.id.image_close1);
            image_delete = (ImageView) v.findViewById(R.id.image_delete);
        }
    }
}