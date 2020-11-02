package com.example.matsapp.Adapters;

import android.content.Context;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.example.matsapp.Fragments.AddNewContactFragment;
import com.example.matsapp.Fragments.ChatFragment;
import com.example.matsapp.Models.Message;
import com.example.matsapp.R;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.util.List;
import java.util.logging.Handler;

public class ChatFragmentAdapter extends RecyclerView.Adapter<ChatFragmentAdapter.CardHolder>{

    private Context mContext;
    private List<Message> messageList;
    private String FRIEND_PHONE_NUMBER;

    private String USER_PHONE_NUMBER;
    private int state;

    private static final int VIEW_TYPE_TEXT_SENT = 1, VIEW_TYPE_TEXT_RECEIVED = 2, VIEW_TYPE_IMAGE_SENT = 3, VIEW_TYPE_IMAGE_RECEIVED = 4,
            VIEW_TYPE_VOICE_SENT = 5, VIEW_TYPE_VOICE_RECEIVED = 6, VIEW_TYPE_SHARE_CONTACT_SENT = 7, VIEW_TYPE_SHARE_CONTACT_RECEIVED = 8;

    private FirebaseDatabase database;
    private DatabaseReference myRef;

    private FirebaseStorage storage;
    private StorageReference stRef;

    private MediaPlayer mediaPlayer;


    public ChatFragmentAdapter(Context mContext, List<Message> messageList, String FRIEND_PHONE_NUMBER) {
        this.mContext = mContext;
        this.messageList = messageList;
        this.FRIEND_PHONE_NUMBER = FRIEND_PHONE_NUMBER;


        USER_PHONE_NUMBER = mContext.getSharedPreferences("userInfo",mContext.MODE_PRIVATE).getString("phoneNum", "1");

        state = 1;

        database = FirebaseDatabase.getInstance();
        myRef = database.getReference();

        storage = FirebaseStorage.getInstance();
        stRef = storage.getReference();




    }


    public class CardHolder extends RecyclerView.ViewHolder{

        private TextView messageAdapter_messageTextField, messageAdapter_messageTime, messageAdapter_voiceTime, messageAdapter_contactName,
                        messageAdapter_contactNum, messageAdapterContactSendMessage_click, messageAdapterContactAdd_click;

        private ImageView messageAdapter_messagePP, messageAdapter_playButton, messageAdapter_stopButton;

        private ImageView messageAdapter_messageImageField;

        private SeekBar messageAdapter_voiceProgress;


        private CardView cardView_aaa;

        public CardHolder(@NonNull View itemView) {
            super(itemView);

            if (state == 1){ // user text message atti ise ...

                messageAdapter_messageTextField = itemView.findViewById(R.id.messageAdapter_messageField_user);
                messageAdapter_messageTime = itemView.findViewById(R.id.cardView_user_time);

            }
            else if (state == 2){ // friend text message atti ise ...

                messageAdapter_messageTextField = itemView.findViewById(R.id.a);
                messageAdapter_messageTime = itemView.findViewById(R.id.cardView_friend_time);
                cardView_aaa = itemView.findViewById(R.id.cardView_aaa);

            }
            else if (state == 3){ // user image message atti ise ...

                messageAdapter_messageImageField = itemView.findViewById(R.id.imageView_cardView_userImageMessage_imageMessage);
                messageAdapter_messageTime = itemView.findViewById(R.id.textView2_cardView_userImageMessage_userTime);
                messageAdapter_messageTextField = itemView.findViewById(R.id.textView_cardView_userImageMessage_textMessageField);

            }
            else if (state == 4){ // friend image message atti ise ...

                messageAdapter_messageImageField = itemView.findViewById(R.id.imageView_cardView_friendImageMessage_imageMessage);
                messageAdapter_messageTime = itemView.findViewById(R.id.textView_cardViewFriendImageMessage_friendTime);
                messageAdapter_messageTextField = itemView.findViewById(R.id.textView_cardView_friendImageMessage_textMessageField);


            }
            else if (state == 5){ // user voice message gonderdi ise .

                messageAdapter_messagePP = itemView.findViewById(R.id.imageView_cardView_userVoiceMessage_userPP);
                messageAdapter_playButton = itemView.findViewById(R.id.imageView_cardView_userVoiceMessage_playButton);
                messageAdapter_messageTime = itemView.findViewById(R.id.textView_cardView_userVoiceMessage_messageClock);
                messageAdapter_voiceTime = itemView.findViewById(R.id.textView_cardView_userVoiceMessage_voiceTime);
                messageAdapter_voiceProgress = itemView.findViewById(R.id.seekBar_cardView_userVoiceMessage_voiceProgress);
                messageAdapter_stopButton = itemView.findViewById(R.id.imageView_cardView_userVoiceMessage_stopButton);

            }
            else if (state == 6){ // friend voice message gonderdi ise .

                messageAdapter_messagePP = itemView.findViewById(R.id.imageView_cardView_friendVoiceMessage_friendPP);
                messageAdapter_playButton = itemView.findViewById(R.id.imageView_cardView_friendVoiceMessage_playButton);
                messageAdapter_messageTime = itemView.findViewById(R.id.textView_cardView_friendVoiceMessage_messageClock);
                messageAdapter_voiceTime = itemView.findViewById(R.id.textView__cardView_friendVoiceMessage__voiceTime);
                messageAdapter_voiceProgress = itemView.findViewById(R.id.seekBar_cardView_friendVoiceMessage_voiceProgress);
                messageAdapter_stopButton = itemView.findViewById(R.id.imageView_cardView_friendVoiceMessage_stopButton);

            }
            else if (state == 7){ // kullanici contact gonderdi ise .

                messageAdapter_messagePP = itemView.findViewById(R.id.imageView_cardView_userShareContact_contactPP);
                messageAdapter_contactName = itemView.findViewById(R.id.textView_cardView_userShareContact_contactName);
                messageAdapter_messageTime = itemView.findViewById(R.id.textView_cardView_userShareContact_messageTime);
                messageAdapterContactSendMessage_click = itemView.findViewById(R.id.textView_cardView_userShareContact_sendMessage);
                messageAdapter_contactNum = itemView.findViewById(R.id.textView_cardView_userShareContact_contactNum);




            }
            else if (state == 8) {// friend contact gonderdi ise .

                messageAdapter_messagePP = itemView.findViewById(R.id.textView_cardView_friendShareContact_contactPP);
                messageAdapter_contactName = itemView.findViewById(R.id.textView_cardView_friendShareContact_contactName);
                messageAdapter_messageTime = itemView.findViewById(R.id.textView_cardView_friendShareContact_messageTime);
                messageAdapterContactSendMessage_click = itemView.findViewById(R.id.textView_cardView_friendShareContact_sendMessage);
                messageAdapterContactAdd_click = itemView.findViewById(R.id.textView_cardView_friendShareContact_add);
                messageAdapter_contactNum = itemView.findViewById(R.id.textView_cardView_friendShareContact_contactNum);

            }
            else {

            }


        }

    }


    @NonNull
    @Override
    public CardHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View itemView;

        if (viewType == VIEW_TYPE_TEXT_SENT){ // kullanici text mesaji gonderdi ise ...

            itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.cardview_user_message_design,parent,false);

            return new CardHolder(itemView);

        }
        else if (viewType == VIEW_TYPE_TEXT_RECEIVED){ // arkadastan text mesaji geldi ise ...

            itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.cardview_friend_message_design,parent,false);

            return new CardHolder(itemView);

        }
        else if (viewType == VIEW_TYPE_IMAGE_SENT){ // kullanici image message gonderdi ise ...

            itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.cardview_user_imagemessage_design,parent,false);

            return new CardHolder(itemView);

        }
        else if(viewType == VIEW_TYPE_IMAGE_RECEIVED){

            itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.cardview_friend_imagemessage_design,parent,false);

            return new CardHolder(itemView);

        }
        else if (viewType == VIEW_TYPE_VOICE_SENT){

            itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.cardview_user_voice_message_design, parent, false);

            return new CardHolder(itemView);

        }
        else if (viewType == VIEW_TYPE_VOICE_RECEIVED){

            itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.cardview_friend_voice_message_design, parent, false);

            return new CardHolder(itemView);

        }
        else if (viewType == VIEW_TYPE_SHARE_CONTACT_SENT){

            itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.cardview_user_sharecontact_design, parent, false);

            return new CardHolder(itemView);

        }
        else if (viewType == VIEW_TYPE_SHARE_CONTACT_RECEIVED){

            itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.cardview_friend_sharecontact_design, parent, false);

            return new CardHolder(itemView);

        }
        else{

        }


        return null;
    }

    @Override
    public void onBindViewHolder(@NonNull CardHolder holder, int position) {

        Message message = messageList.get(position);

        if (message.getMessageType().equals("text")) { // text message ise ...
            holder.messageAdapter_messageTextField.setText(message.getMessageText());

        }
        else if (message.getMessageType().equals("image") || message.getMessageType().equals("imageText")){ // image message ise ...

            setImageMessageToAttribute(holder, message);

        }
        else if(message.getMessageType().equals("voice")){ // voice message ise ...

            setVoiceMessageToAttribute(holder, message);

        }
        else if (message.getMessageType().equals("contact")){

            setContactMessageToAttribute(holder, message);


            holder.messageAdapterContactSendMessage_click.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    openChatFragmentForSharedContact(message);

                }
            });

            if (state == VIEW_TYPE_SHARE_CONTACT_RECEIVED) { // kisi ekle sadece gonderilen kisi de cikacak .
                holder.messageAdapterContactAdd_click.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        openAddNewContactFragmentForSharedContact(message);

                    }
                });
            }


        }

        holder.messageAdapter_messageTime.setText(message.getMessageDate());
    }

    @Override
    public int getItemCount() {
        return messageList.size();
    }


    @Override
    public int getItemViewType(int position) {



        if (messageList.get(position).getMessageFrom().equals(USER_PHONE_NUMBER)){ // kullanici gondermisse .



            if (messageList.get(position).getMessageType().equals("text")){ // eger text message ise .

                state = 1;

                return VIEW_TYPE_TEXT_SENT;

            }
            else if (messageList.get(position).getMessageType().equals("image") || messageList.get(position).getMessageType().equals("imageText")){ // eger image message ise .

                state= 3;

                return VIEW_TYPE_IMAGE_SENT;

            }else if (messageList.get(position).getMessageType().equals("voice")){ // eger voice message ise .

                state = 5;

                return VIEW_TYPE_VOICE_SENT;

            }
            else if (messageList.get(position).getMessageType().equals("contact")){

                state = 7;

                return VIEW_TYPE_SHARE_CONTACT_SENT;

            }


        }
        else{ // arkadastan gelmisse .

            if (messageList.get(position).getMessageType().equals("text")){ // eger text message ise .

                state = 2;

                return VIEW_TYPE_TEXT_RECEIVED;

            }
            else if(messageList.get(position).getMessageType().equals("image") || messageList.get(position).getMessageType().equals("imageText")){ // eger image message ise .

                state = 4;

                return VIEW_TYPE_IMAGE_RECEIVED;

            }
            else if (messageList.get(position).getMessageType().equals("voice")){ // eger voice message ise .

                state = 6;

                return VIEW_TYPE_VOICE_RECEIVED;

            }
            else if (messageList.get(position).getMessageType().equals("contact")){

                state = 8;

                return VIEW_TYPE_SHARE_CONTACT_RECEIVED;

            }



        }

return 10;
    }


    /**
     * kisiyi rehbere eklemek icin kisi ekleme fragment i acilir .
     */
    public void openAddNewContactFragmentForSharedContact(Message message){

        String[] contactInfo = message.getMessageText().trim().split(",");
        String contactName = contactInfo[0]; // --> 0. indis isim tutar .
        String contactNum = contactInfo[1]; // --> 1. indis numara tutar .

        AddNewContactFragment addNewContactFragment = new AddNewContactFragment();
        Bundle bundle = new Bundle();
        bundle.putString("contactName", contactName);
        bundle.putString("contactNum", contactNum);
        addNewContactFragment.setArguments(bundle);
        ((AppCompatActivity)mContext).getSupportFragmentManager().beginTransaction().replace(R.id.fragment_holder_userLoggedInActivity, addNewContactFragment).commit();

    }


    /**
     * mesaj gondere basildiginda numara app e kayitli ise chat fragment i acar .
     * @param message
     */
    public void openChatFragmentForSharedContact(Message message){

        String[] contactInfo = message.getMessageText().trim().split(",");
        String contactName = contactInfo[0]; // --> 0. indis isim tutar .
        String contactNum = contactInfo[1]; // --> 1. indis numara tutar .



        myRef.child("Users").child(contactNum).child("userPhone").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                try{

                    String tmp = snapshot.getValue().toString(); // boyle bilgiye erisim varsa kullanici app e kayitli demektir .

                    ChatFragment chatFragment = new ChatFragment();
                    Bundle bundle = new Bundle();
                    bundle.putString("friendNum", contactNum);
                    bundle.putString("friendName", contactName);
                    chatFragment.setArguments(bundle);
                    ((AppCompatActivity)mContext).getSupportFragmentManager().beginTransaction().replace(R.id.fragment_holder_userLoggedInActivity, chatFragment).commit();

                }
                catch (Exception e){

                    Log.e("excptn opnChtFntfrCntct", e.getMessage());
                    Toast.makeText(mContext, contactNum + " numarası uygulamaya kayıtlı değil ...", Toast.LENGTH_SHORT).show();

                }


            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


    }


    /**
     * Share Contact mesaji icin gerekli bilgileri gerekli gorsel nesnelere yerlestirir .
     * @param holder
     * @param message
     */
    public void setContactMessageToAttribute(CardHolder holder, Message message){

        String[] contactInfo = message.getMessageText().trim().split(",");

        String contactName = contactInfo[0]; // ---> 0. indiste kişi adi var .
        String contactNum = contactInfo[1]; // ---> 1. indiste kisi num var .

        holder.messageAdapter_contactName.setText(contactName);
        holder.messageAdapter_contactNum.setText(contactNum);

        setPPforShareContactMessage(holder, contactNum);


    }


    /**
     * share contact icin userPP si varsa gorsel nesneye yerlestirir .
     * @param holder
     * @param contactNum
     */
    public void setPPforShareContactMessage(CardHolder holder, String contactNum){


        myRef.child("Users").child(contactNum).child("userPP").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                try { // eger app e kayitli ve pp si  var ise ...

                    String userPPKey = snapshot.getValue().toString();

                    stRef.child("UsersPictures").child(userPPKey).getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {

                            Picasso.get().load(uri).into(holder.messageAdapter_messagePP);

                        }
                    });

                }
                catch (Exception e){ // yoksa ...

                    Log.e("excptn setContactMssg",e.getMessage());

                }




            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


    }


    /**
     * image ve ya imageText e gore yerlestirme yapar .
     */
    public void setImageMessageToAttribute(CardHolder holder, Message message){

        if (message.getMessageType().equals("image")){ // sadece resim gonderildi ise ...

            String imageKey = message.getMessageText();

            stRef.child("UsersMessagesPictures").child(imageKey).getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                @Override
                public void onSuccess(Uri uri) {

                    Picasso.get().load(uri).into(holder.messageAdapter_messageImageField);

                }
            });


        }else{ // resimle birlikte text mesaji gonderildi ise ...

            String imageKey = message.getMessageText(); // burada virgül ile ayrili --> slit edip iki indisli array e donusecek .
                                                            // [0] --> imageKey , [1] --> textMessage .
            String[] arr = imageKey.split(",");

            holder.messageAdapter_messageTextField.setText(arr[1]);

            stRef.child("UsersMessagesPictures").child(arr[0]).getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() { // *************** buraya dikkat ...
                @Override
                public void onSuccess(Uri uri) {

                    Picasso.get().load(uri).into(holder.messageAdapter_messageImageField);

                }
            });

        }



    }


    /**
     * ses mesaji icin verilen duration u saniye cinsine cevirir .
     * @param milliSeconds
     * @return
     */
    public String milliSecondsToTimer(long milliSeconds){

        String timerString = "";
        String secondsString;

        int hours = (int) (milliSeconds / (1000 * 60 *60));
        int minutes = (int) (milliSeconds % (1000 * 60 *60)) / (1000 * 60);
        int seconds = (int) (milliSeconds % (1000 * 60 * 60) % (1000 * 60) / 1000);

        if (hours > 0){
            timerString = hours + ":";
        }

        if (seconds < 10){
            secondsString = "0" + seconds;
        }
        else {
            secondsString = "" + seconds;
        }

        timerString = timerString + minutes + ":" + secondsString;
        return  timerString;

    }


    /**
     * atilan sesin sesin suresini gerekli gorsel nesneye yerlestirir .
     */
    public void setVoiceTime(CardHolder holder, String messageText){

        mediaPlayer = new MediaPlayer();

        try{
            mediaPlayer.setDataSource(messageText);
            mediaPlayer.prepare();
            holder.messageAdapter_voiceTime.setText(milliSecondsToTimer(mediaPlayer.getDuration()));
        }
        catch (Exception e){

        }
        mediaPlayer = null;

    }


    /**
     * voice message icin gerekli bilgileri gorsel nesnelere yerlestirir .
     * @param holder
     * @param message
     */
    public void setVoiceMessageToAttribute(CardHolder holder, Message message){

        getUserPP(holder);

        setVoiceTime(holder, message.getMessageText());

        holder.messageAdapter_playButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                startVoice(holder, message.getMessageText());
                changeSeekbar(holder);
                holder.messageAdapter_playButton.setVisibility(View.INVISIBLE);
                holder.messageAdapter_stopButton.setVisibility(View.VISIBLE);


            }
        });

        holder.messageAdapter_stopButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                stopVoice();
                holder.messageAdapter_stopButton.setVisibility(View.INVISIBLE);
                holder.messageAdapter_playButton.setVisibility(View.VISIBLE);

            }
        });

    }


    /**
     * ses kaynagini mediplayer a yerlestirir ve seekbar a tiklamaya gore sesi kaydirir .
     * @param holder
     * @param voiceUrl
     */
    // bug var aynı anda iki sesi çalıştırınca ikisi de calisiyor. Recyc de bir önceki nesneye ulaşmam gerekli . *** ---> BUG VAR .....
    public void startVoice(CardHolder holder, String voiceUrl) {


        mediaPlayer = new MediaPlayer();

        try {

            mediaPlayer.setDataSource(voiceUrl);
            mediaPlayer.prepare();

            mediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                @Override
                public void onPrepared(MediaPlayer mp) {

                    holder.messageAdapter_voiceProgress.setMax(mediaPlayer.getDuration());
                    mediaPlayer.start();
                    changeSeekbar(holder);

                }
            });

            holder.messageAdapter_voiceProgress.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                @Override
                public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

                    if (fromUser){

                        mediaPlayer.seekTo(progress);

                    }

                }

                @Override
                public void onStartTrackingTouch(SeekBar seekBar) {

                }

                @Override
                public void onStopTrackingTouch(SeekBar seekBar) {

                }
            });




        } catch (IOException e) {
            Log.e("startVoice exception", "prepare() failed");
        }
    }


    public void stopVoice() {

        if (mediaPlayer.isPlaying()) {
            mediaPlayer.pause();
        }

    }


    /**
     * ses teki ilerlemeyi es zamanli olarak seekBar a yansitir .
     * @param holder
     */
    public void changeSeekbar(CardHolder holder){

        holder.messageAdapter_voiceProgress.setProgress(mediaPlayer.getCurrentPosition());

        holder.messageAdapter_voiceTime.setText(milliSecondsToTimer(mediaPlayer.getCurrentPosition()));

        if (mediaPlayer.isPlaying()){

            new android.os.Handler().postDelayed(new Runnable() {
                @Override
                public void run() {

                    changeSeekbar(holder);


                }
            },1000);

        }
        else{

            holder.messageAdapter_playButton.setVisibility(View.VISIBLE);
            holder.messageAdapter_stopButton.setVisibility(View.INVISIBLE);
            holder.messageAdapter_voiceProgress.setProgress(0);

        }


    }



    /**
     * kullanici pp sini gerekli gorsel nesneye yerlestirir .
     */
    public void getUserPP(CardHolder holder){

        myRef.child("Users").child(USER_PHONE_NUMBER).child("userPP").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                String userPPKey = snapshot.getValue().toString();

                stRef.child("UsersPictures").child(userPPKey).getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {

                        Picasso.get().load(uri).into(holder.messageAdapter_messagePP);

                    }
                });


            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


    }




}
