package ba.sum.fsre.dnevnikraspolozenja.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import ba.sum.fsre.dnevnikraspolozenja.R;

import java.util.List;

public class AvatarAdapter extends RecyclerView.Adapter<AvatarAdapter.AvatarViewHolder> {

    private Context context;
    private List<String> avatarUrls;
    public int selectedPosition = 0;

    public AvatarAdapter(Context context, List<String> avatarUrls) {
        this.context = context;
        this.avatarUrls = avatarUrls;
    }

    public int getSelectedPosition() {
        return selectedPosition;
    }

    public String getSelectedAvatarUrl() {
        return avatarUrls.get(selectedPosition);
    }

    @NonNull
    @Override
    public AvatarViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.avatar_item, parent, false);
        return new AvatarViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AvatarViewHolder holder, int position) {
        Glide.with(context)
                .load(avatarUrls.get(position))
                .into(holder.avatarImage);
    }

    @Override
    public int getItemCount() {
        return avatarUrls.size();
    }

    static class AvatarViewHolder extends RecyclerView.ViewHolder {
        ImageView avatarImage;

        public AvatarViewHolder(@NonNull View itemView) {
            super(itemView);
            avatarImage = itemView.findViewById(R.id.avatarImage);
        }
    }
}
