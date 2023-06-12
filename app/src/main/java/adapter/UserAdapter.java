package adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.chatapp.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.squareup.picasso.Picasso;

import java.util.List;

import Model.contacts;
import de.hdodenhof.circleimageview.CircleImageView;

public class UserAdapter extends RecyclerView.Adapter<UserAdapter.ViewHolser> {

    private List<contacts> mUsers;
    private Context mcontext;
    private FirebaseUser firebaseUser;

    public UserAdapter(List<contacts> mUsers, Context mcontext) {
        this.mUsers = mUsers;
        this.mcontext = mcontext;
    }

    @NonNull
    @Override
    public UserAdapter.ViewHolser onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mcontext).inflate(R.layout.friend_item ,parent, false);
        return new UserAdapter.ViewHolser(view);
    }

    @Override
    public void onBindViewHolder(@NonNull UserAdapter.ViewHolser holder, int position) {
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        contacts contact = mUsers.get(position);
        holder.usernameTextView.setText(contact.getUsername());
        if(contact.getImageURL().equals("default")){
            holder.ProfilePicture.setImageResource(R.drawable.ic_profile);
        }else {
            Picasso.get().load(contact.getImageURL()).into(holder.ProfilePicture);
        }
    }

    @Override
    public int getItemCount() {
        return mUsers.size();
    }

    public class ViewHolser extends RecyclerView.ViewHolder{

        public TextView usernameTextView;
        public CircleImageView ProfilePicture;

        public ViewHolser(@NonNull View itemView) {
            super(itemView);
            ProfilePicture = itemView.findViewById(R.id.profile_picture);
            usernameTextView = itemView.findViewById(R.id.username);
        }
    }
}
