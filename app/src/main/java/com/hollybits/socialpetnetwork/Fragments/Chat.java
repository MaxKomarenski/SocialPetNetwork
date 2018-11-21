package com.hollybits.socialpetnetwork.Fragments;

import android.graphics.Typeface;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.OvershootInterpolator;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.hollybits.socialpetnetwork.R;
import com.hollybits.socialpetnetwork.activity.FragmentDispatcher;
import com.hollybits.socialpetnetwork.activity.MainActivity;
import com.hollybits.socialpetnetwork.adapters.MessageAdapter;
import com.hollybits.socialpetnetwork.data_queues.MessageQueue;
import com.hollybits.socialpetnetwork.helper.MessageObserver;
import com.hollybits.socialpetnetwork.models.Contact;
import com.hollybits.socialpetnetwork.models.Message;
import com.hollybits.socialpetnetwork.models.User;
import com.hollybits.socialpetnetwork.models.UserInfo;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.paperdb.Paper;
import jp.wasabeef.recyclerview.animators.SlideInUpAnimator;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link Chat.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link Chat#newInstance} factory method to
 * create an instance of this fragment.
 */
public class Chat extends Fragment implements MessageObserver {

    @BindView(R.id.message_chat_recycler_view)
    RecyclerView chatRecyclerView;

    @BindView(R.id.write_a_message_edit_text)
    EditText writeMessageEditText;

    @BindView(R.id.back_to_contacts_button)
    Button backToContact;

    @BindView(R.id.send_message_button)
    ImageButton sendMessageButton;

    @BindView(R.id.name_in_chat_fragment)
    TextView nameOfFriend;

    @BindView(R.id.online_and_not_online)
    TextView onlineOrNotOnlineText;

    @BindView(R.id.indicator)
    ImageView greenIndicator;

    MessageAdapter messageAdapter;
    List<Message> messages;

    private Long friendId;
    private ScheduledExecutorService onlineTracker;
    private Typeface messageTextFont;
    private Typeface mainFont;

    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

    public Chat() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment Chat.
     */
    // TODO: Rename and change types and number of parameters
    public static Chat newInstance(String param1, String param2) {
        Chat fragment = new Chat();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_chat, container, false);
        ButterKnife.bind(this, view);

        friendId = Paper.book().read(MainActivity.ID_OF_FRIEND);
        MessageQueue.getInstance().addObserver(this);

        textEdition();

        onlineTracker = Executors.newScheduledThreadPool(1);
        onlineTracker.scheduleAtFixedRate(Chat.this::checkIfUserIsOnline,0, 1, TimeUnit.MINUTES);

        listeners();

        LinearLayoutManager layoutManager = new LinearLayoutManager(this.getContext());
        layoutManager.setStackFromEnd(true);
        chatRecyclerView.setLayoutManager(layoutManager);
        SlideInUpAnimator animator = new SlideInUpAnimator(new OvershootInterpolator(1f));
        chatRecyclerView.setItemAnimator(animator);

        getAllMessages();

        return view;
    }

    private void textEdition(){
        messageTextFont = Typeface.createFromAsset(this.getActivity().getAssets(), "fonts/HelveticaNeueCyr-Roman.otf");
        mainFont = Typeface.createFromAsset(this.getActivity().getAssets(), "fonts/911Fonts.com_CenturyGothicBold__-_911fonts.com_fonts_pMgo.ttf");
        nameOfFriend.setText(Paper.book().read(MainActivity.NAME_OF_FRIEND));
        nameOfFriend.setTypeface(mainFont);
    }

    private void listeners(){

        sendMessageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!writeMessageEditText.getText().toString().equals("")){
                    sendMessageToTheServer(writeMessageEditText.getText().toString());
                    writeMessageEditText.setText("");
                    chatRecyclerView.scrollToPosition(messageAdapter.getItemCount() - 1);
                    messageAdapter.notifyDataSetChanged();
                }
            }
        });

        nameOfFriend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getAllInformationOfChosenUser(friendId);
            }
        });

        backToContact.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentDispatcher.launchFragment(Messages.class);
            }
        });
    }

    private void getAllInformationOfChosenUser(Long id){
        User currentUser = Paper.book().read(MainActivity.CURRENTUSER);
        Map<String, String> authorisationCode = new HashMap<>();
        authorisationCode.put("authorization", currentUser.getAuthorizationCode());
        MainActivity.getServerRequests().getInformationOfAnotherUser(authorisationCode, id).enqueue(new Callback<UserInfo>() {
            @Override
            public void onResponse(Call<UserInfo> call, Response<UserInfo> response) {
                Paper.book().write(MainActivity.CURRENT_CHOICE, response.body());
                FragmentDispatcher.launchFragment(FriendAccount.class);

            }

            @Override
            public void onFailure(Call<UserInfo> call, Throwable t) {

            }
        });
    }

    private void checkIfUserIsOnline(){


        MainActivity.getServerRequests().getUserLastActiveTime(getAuthorizationCode(), friendId).enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {

                String time = response.body().replaceAll("%", " ");
                Timestamp timestamp = Timestamp.valueOf(time.replaceAll("\\^", ":"));

                long five_minutes = 5 * 60 * 1000;
                long currentTime = System.currentTimeMillis();

                try{
                    if(currentTime - timestamp.getTime() < five_minutes){

                        Chat.this.getActivity().runOnUiThread(() -> {
                            greenIndicator.setVisibility(View.VISIBLE);
                            onlineOrNotOnlineText.setText("online");
                            onlineOrNotOnlineText.setTextColor(Objects.requireNonNull(getActivity()).getResources().getColor(R.color.online));
                        });

                    }else {

                        Chat.this.getActivity().runOnUiThread(() -> {

                            greenIndicator.setVisibility(View.INVISIBLE);
                            onlineOrNotOnlineText.setText("not online");
                            onlineOrNotOnlineText.setTextColor(Objects.requireNonNull(getActivity()).getResources().getColor(R.color.not_online));

                        });
                    }
                }catch (NullPointerException e){

                }


            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {

            }
        });

    }

    private Map<String, String> getAuthorizationCode(){
        User currentUser = Paper.book().read(MainActivity.CURRENTUSER);
        Map<String, String> authorisationCode = new HashMap<>();
        authorisationCode.put("authorization", currentUser.getAuthorizationCode());
        return authorisationCode;
    }

    private void sendMessageToTheServer(String text){
        User currentUser = Paper.book().read(MainActivity.CURRENTUSER);
        Long timestamp = System.currentTimeMillis();
        Message message = new Message(text, friendId, MainActivity.getCurrentUser().getId(),false);

        MainActivity.getServerRequests().sendMessage(getAuthorizationCode(),
                                                     message,
                                                     currentUser.getId(),
                                                     timestamp).enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                Log.d("response", String.valueOf(response.code()));
                addMessageToPaperBook(message);
            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {

            }
        });

        message.setTimestamp(new Timestamp(timestamp));
        messageAdapter.add(message);

    }

    private void getAllMessages(){

        messages = Paper.book(MainActivity.MESSAGE_BOOK).read(friendId.toString());

        if(messages == null){
            User currentUser = Paper.book().read(MainActivity.CURRENTUSER);
            MainActivity.getServerRequests().getAllMessagesWithCurrentFriend(getAuthorizationCode(), currentUser.getId(), friendId).enqueue(new Callback<List<Message>>() {
                @Override
                public void onResponse(Call<List<Message>> call, Response<List<Message>> response) {
                    messages = new ArrayList<>();

                    if (response.body() != null){
                        messages.addAll(response.body());
                    }
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                        messages.sort((o1, o2)-> o1.getTimestamp().compareTo(o2.getTimestamp()));
                    }else {
                        Collections.sort(messages,new Comparator<Message>() {
                            @Override
                            public int compare(Message o1, Message o2) {
                                return o1.getTimestamp().compareTo(o2.getTimestamp());
                            }
                        });
                    }

                    messageAdapter = new MessageAdapter(messages, messageTextFont);
                    chatRecyclerView.setAdapter(messageAdapter);
                    messageAdapter.notifyDataSetChanged();
                    createNewPaperBook();
                    addAllMessagesToPaperBook(messages);
                }

                @Override
                public void onFailure(Call<List<Message>> call, Throwable t) {

                }
            });
        }else {

            User user = Paper.book().read(MainActivity.CURRENTUSER);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                messages.sort((o1, o2)-> o1.getTimestamp().compareTo(o2.getTimestamp()));
            }else {
                Collections.sort(messages, (o1, o2) -> o1.getTimestamp().compareTo(o2.getTimestamp()));
            }
            messageAdapter = new MessageAdapter(messages, messageTextFont);
            chatRecyclerView.setAdapter(messageAdapter);
            getAllUnreadMessages(friendId, user.getId());
            messageAdapter.notifyDataSetChanged();

        }

    }

    private void addMessageToPaperBook(Message m){
        List<Message> messageList = Paper.book(MainActivity.MESSAGE_BOOK).read(friendId.toString());
        messageList.add(m);
        Paper.book(MainActivity.MESSAGE_BOOK).write(friendId.toString(), messageList);
    }

    private void addAllMessagesToPaperBook(List<Message> list){
        List<Message> messageList = Paper.book(MainActivity.MESSAGE_BOOK).read(friendId.toString());
        for (Message m:
             list) {
            messageList.add(m);
        }
        Paper.book(MainActivity.MESSAGE_BOOK).write(friendId.toString(), messageList);
    }

    private void createNewPaperBook(){
        Long friendId = Paper.book().read(MainActivity.ID_OF_FRIEND);
        List<Message> mList = new ArrayList<>();
        Paper.book(MainActivity.MESSAGE_BOOK).write(friendId.toString(), mList);
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }


    @Override
    public void onDetach() {
        super.onDetach();
        MessageQueue.getInstance().removeObserver(this);
        onlineTracker.shutdown();
        mListener = null;

        changeLastMessageInContactFragment();
    }

    @Override
    public void update() {

        try {
            Message message = MessageQueue.getInstance().get(friendId);

            //if(message != null){
                System.err.println("Updating message: "+ message.getMessage());
                messageAdapter.add(message);

                addMessageToPaperBook(message);
                getActivity().runOnUiThread(() -> {messageAdapter.notifyDataSetChanged();
                chatRecyclerView.scrollToPosition(messageAdapter.getItemCount() - 1);   });
                FragmentDispatcher.decCounter(1);
                makeThisMassageRead(message.getFriendsId());
            //}

        }catch (NullPointerException e){
            e.printStackTrace();
        }

    }

    private void changeLastMessageInContactFragment(){

        if(messages.size() != 0){
            List<Contact> contactList = Paper.book().read(MainActivity.CONTACT_LIST);

            try{
                for (int i = 0; i < contactList.size(); i++){
                    if (contactList.get(i).getFriendId().equals(friendId)){
                        contactList.get(i).setLastMessage(messages.get(messages.size() - 1).getMessage());
                    }
                }

                Paper.book().write(MainActivity.CONTACT_LIST, contactList);
            }catch (NullPointerException e){

            }
        }


    }

    private void getAllUnreadMessages(Long friend, Long user){
        MainActivity.getServerRequests().getAllUnreadMessages(getAuthorizationCode(), friend, user).enqueue(new Callback<List<Message>>() {
            @Override
            public void onResponse(Call<List<Message>> call, Response<List<Message>> response) {

                List<Message> m_s = response.body();
                if( m_s != null && m_s.size() > 0){
                    FragmentDispatcher.decCounter(m_s.size());
                    for (Message message: response.body()){
                        messageAdapter.add(message);
                        addMessageToPaperBook(message);
                        messageAdapter.notifyDataSetChanged();
                    }
                    makeThisMassageRead(m_s.get(0).getFriendsId());
                }

            }

            @Override
            public void onFailure(Call<List<Message>> call, Throwable t) {

            }
        });
    }

    private void makeThisMassageRead(Long friendsId){

        MainActivity.getServerRequests().makeLastMessageRead(getAuthorizationCode(), friendsId, MainActivity.getCurrentUser().getId()).enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
            }
            @Override
            public void onFailure(Call<String> call, Throwable t) {
            }
        });
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }
}
