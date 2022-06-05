package com.seyeong.youtube_block_application2.adapter;

import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

public class CustomAdapter extends BaseAdapter {
    @Override
    public int getCount() {
        return 0;
    }

    @Override
    public Object getItem(int i) {
        return null;
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        return null;
    }

    /*private Context context;
    private List<CustomView> customViewList;
    public String downloadStatus = "다운로드중...";
    public int public_progress = 0;
    public String public_percent;

    public CustomAdapter(Context context, List<CustomView> customViewList) {
        this.context = context;
        this.customViewList = customViewList;
    }

    public class ViewHolder {
        public ImageView thumbnail;
        public TextView title;
        public int int_progress = public_progress;
        public ProgressBar progressBar;
        public String string_persent = public_percent;
        public TextView progressPersent;
        public TextView fileSize;
        public Bitmap mBitmap;
        public TextView tvDownloadStatus;
        public String holder_downloadStatus = downloadStatus;
    }

    @Override
    public int getCount() {
        return customViewList.size();
    }

    @Override
    public Object getItem(int i) {
        return customViewList.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {

        ViewHolder holder = null;
        if (view == null) {
            LayoutInflater vi = (LayoutInflater) getSystemService(
                    Context.LAYOUT_INFLATER_SERVICE);
            view = vi.inflate(R.layout.custom_view, null);
            //final TextView work = (TextView) view.findViewById(R.id.titl);
            holder = new ViewHolder();
            holder.thumbnail = (ImageView) view.findViewById(R.id.thumbnail);
            holder.title = (TextView) view.findViewById(R.id.title);
            holder.progressBar = (ProgressBar) view.findViewById(R.id.progress_bar);
            holder.progressPersent = (TextView) view.findViewById(R.id.progress_percent);
            holder.fileSize = (TextView) view.findViewById(R.id.fileSize);
            holder.tvDownloadStatus = (TextView) view.findViewById(R.id.downloadStatus);

            view.setTag(holder);


        } else {
            holder = (ViewHolder) view.getTag();
        }

        CustomView country = customList.get(i);

        mDbOpenHelper.openR();
        Map<String, String> map = mDbOpenHelper.selectProgress();
        mDbOpenHelper.close();

        holder.thumbnail.setImageBitmap(country.getmBitmap());
        holder.title.setText(country.getTitle()+"  ");
        holder.progressBar.setProgress(Integer.parseInt(map.get("progress"+i)));
        holder.progressPersent.setText(map.get("progress"+i) + "%");
        holder.fileSize.setText(country.getFileSize());
        holder.tvDownloadStatus.setText(map.get("isdownload"+i));
        holder.title.setTag(country);
        return view;
    }*/
}