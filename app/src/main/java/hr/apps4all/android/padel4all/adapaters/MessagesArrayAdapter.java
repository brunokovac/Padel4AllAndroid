package hr.apps4all.android.padel4all.adapaters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.text.Layout;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.Calendar;
import java.util.List;
import java.util.TimeZone;

import hr.apps4all.android.padel4all.LookingGameFragment;
import hr.apps4all.android.padel4all.MyApplication;
import hr.apps4all.android.padel4all.OrganizerGameFragment;
import hr.apps4all.android.padel4all.R;
import hr.apps4all.android.padel4all.models.Game;
import hr.apps4all.android.padel4all.models.Message;
import hr.apps4all.android.padel4all.models.Player;

/**
 * Created by Bruno on 4.1.2018..
 */

public class MessagesArrayAdapter extends ArrayAdapter<Message> {

    private Context context;
    private List<Message> messages;

    public MessagesArrayAdapter(Context context, List<Message> messages){
        super(context, -1, messages);
        this.context = context;
        this.messages = messages;
    }

    @NonNull
    @Override
    public View getView(final int position, @Nullable final View convertView, @NonNull ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        final Message message = messages.get(position);

        Player player = (Player) ((MyApplication) (((AppCompatActivity)context).getApplication())).getActivePerson();
        View rowView;
        if (player.getUsername().equals(message.getSenderInfo().split("\\?")[0])){
            rowView = inflater.inflate(R.layout.sender_chat_message_item, parent, false);
        }else{
            rowView = inflater.inflate(R.layout.chat_message_item, parent, false);
        }

        EditText messageField = (EditText) rowView.findViewById(R.id.chatMessageItemMessageField);
        TextView messageInfo = (TextView) rowView.findViewById(R.id.chatMessageItemInfoField);

        DisplayMetrics metrics = new DisplayMetrics();
        ((AppCompatActivity) context).getWindowManager().getDefaultDisplay().getMetrics(metrics);
        messageField.setMaxWidth(metrics.widthPixels / 10 * 7);

        messageField.setText(message.getContent());

        Calendar cal = Calendar.getInstance(TimeZone.getDefault());
        cal.setTime(message.getSendingTime());

        messageInfo.setText(String.format("%s (%d.%d.%d. %d:%02dh)",
                message.getSenderInfo().split("\\?")[1],
                cal.get(Calendar.DAY_OF_MONTH), cal.get(Calendar.MONTH) + 1, cal.get(Calendar.YEAR),
                cal.get(Calendar.HOUR_OF_DAY), cal.get(Calendar.MINUTE)));

        return rowView;
    }
}
