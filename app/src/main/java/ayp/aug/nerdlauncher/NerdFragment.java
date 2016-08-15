package ayp.aug.nerdlauncher;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.annotation.StringDef;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * Created by Hattapong on 8/15/2016.
 */
public class NerdFragment extends Fragment {
    private static final String TAG = "NerdFragment";

    public static NerdFragment newInstance() {

        Bundle args = new Bundle();

        NerdFragment fragment = new NerdFragment();
        fragment.setArguments(args);
        return fragment;
    }

    RecyclerView mRecyclerView;
    List<ResolveInfo> mActivities;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_nerd,container,false);

        mRecyclerView = (RecyclerView)v.findViewById(R.id.nerd_recycler_view);
        mRecyclerView.setLayoutManager(new GridLayoutManager(getActivity(),4));

        setupAdapter();
        mRecyclerView.setAdapter(new NerdAdapter(mActivities));

        return v;
    }

    private void setupAdapter(){
        Intent startupIntent = new Intent(Intent.ACTION_MAIN);
        startupIntent.addCategory(Intent.CATEGORY_LAUNCHER);

        PackageManager pm = getActivity().getPackageManager();
        List<ResolveInfo> activities = pm.queryIntentActivities(startupIntent,0);

        Log.i(TAG,"Found " + activities.size() + " activities");

        Collections.sort(activities, new Comparator<ResolveInfo>() {
            @Override
            public int compare(ResolveInfo a, ResolveInfo b) {
                PackageManager pm = getActivity().getPackageManager();

                String labelA = a.loadLabel(pm).toString();
                String labelB = b.loadLabel(pm).toString();


                return String.CASE_INSENSITIVE_ORDER.compare(labelA,labelB);
            }
        });

        mActivities = activities;
    }

    class NerdViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        private TextView mNameTextView;
        private ImageView mImageView;
        private ResolveInfo mResolveInfo;

        public NerdViewHolder(View itemView){
            super(itemView);

            mNameTextView = (TextView)itemView.findViewById(R.id.text1);
            mImageView = (ImageView)itemView.findViewById(R.id.icon);
        }

        public void bindAcitivity(ResolveInfo resolveInfo) {
            mResolveInfo = resolveInfo;
            PackageManager pm = getActivity().getPackageManager();
            CharSequence activitiesName = resolveInfo.loadLabel(pm);

            mImageView.setImageDrawable(resolveInfo.loadIcon(pm));
            mNameTextView.setText(activitiesName);
            mNameTextView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            ActivityInfo activityInfo = mResolveInfo.activityInfo;

            Intent i = new Intent(Intent.ACTION_MAIN);
            i.setClassName(activityInfo.applicationInfo.packageName,activityInfo.name);

            Log.i(TAG,"Launching : "+ activityInfo.applicationInfo.packageName + " --> " + activityInfo.name);
            i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(i);
        }
    }

    class NerdAdapter extends RecyclerView.Adapter<NerdViewHolder>{
        List<ResolveInfo> mActivities;

        public NerdAdapter(List<ResolveInfo> mActivities) {
            this.mActivities = mActivities;
        }

        @Override
        public NerdViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View itemView = LayoutInflater.from(getActivity()).inflate(R.layout.item_recycler_view,parent,false);
            return new NerdViewHolder(itemView);
        }

        @Override
        public void onBindViewHolder(NerdViewHolder holder, int position) {
            ResolveInfo resolveInfo = mActivities.get(position);
            holder.bindAcitivity(resolveInfo);

        }

        @Override
        public int getItemCount() {
            return mActivities.size();
        }
    }
}
