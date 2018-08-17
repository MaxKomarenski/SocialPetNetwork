package com.hollybits.socialpetnetwork.Fragments;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
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

import com.hollybits.socialpetnetwork.R;
import com.hollybits.socialpetnetwork.activity.MainActivity;
import com.hollybits.socialpetnetwork.adapters.MessageAdapter;
import com.hollybits.socialpetnetwork.models.Message;
import com.hollybits.socialpetnetwork.models.User;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
public class Chat extends Fragment {

    @BindView(R.id.message_chat_recycler_view)
    RecyclerView chatRecyclerView;

    @BindView(R.id.write_a_message_edit_text)
    EditText writeMessageEditText;

    @BindView(R.id.send_message_button)
    Button sendMessageButton;

    MessageAdapter messageAdapter;
    List<Message> messages;

    private Long friendId;

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

        sendMessageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!writeMessageEditText.getText().toString().equals("")){
                    sendMessageToTheServer(writeMessageEditText.getText().toString());
                }
            }
        });


        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this.getContext());
        chatRecyclerView.setLayoutManager(layoutManager);
        SlideInUpAnimator animator = new SlideInUpAnimator(new OvershootInterpolator(1f));
        chatRecyclerView.setItemAnimator(animator);

        getAllMessages();


        return view;
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
        Message message = new Message(text, friendId, false);

        messageAdapter.add(message);


        MainActivity.getServerRequests().sendMessage(getAuthorizationCode(),
                                                     message,
                                                     currentUser.getId(),
                                                     timestamp).enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                Log.d("response", response.body());
            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {

            }
        });

        Timestamp timestamp1 = new Timestamp(System.currentTimeMillis());
        message.setTimestamp(timestamp1);
        addMessageToPaperBook(message);
    }

    private void getAllMessages(){

        messages = Paper.book(MainActivity.MESSAGE_BOOK).read(friendId.toString());

        if(messages == null){
            User currentUser = Paper.book().read(MainActivity.CURRENTUSER);
            MainActivity.getServerRequests().getAllMessagesWithCurrentFriend(getAuthorizationCode(), currentUser.getId(), friendId).enqueue(new Callback<List<Message>>() {
                @Override
                public void onResponse(Call<List<Message>> call, Response<List<Message>> response) {
                    messages = new ArrayList<>();
                    messages.addAll(response.body());
                    messageAdapter = new MessageAdapter(messages);
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
            messageAdapter = new MessageAdapter(messages);
            chatRecyclerView.setAdapter(messageAdapter);
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
        mListener = null;
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
