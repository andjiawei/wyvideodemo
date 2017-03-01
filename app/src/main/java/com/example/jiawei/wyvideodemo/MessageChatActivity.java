package com.example.jiawei.wyvideodemo;

import android.graphics.Rect;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.jiawei.wyvideodemo.utils.Utils;

import java.util.ArrayList;

public class MessageChatActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private Button btnMessage;
    private LinearLayout llEdit;
    private MyAdapter adapter;
   private ArrayList<String>  messageList=new ArrayList<>();
    private EditText etMessage;
    private View view;
    private double lastRootInvisibleHeight;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message_chat);
        view = getWindow().getDecorView();
        recyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setStackFromEnd(true);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(layoutManager);
        btnMessage = (Button) findViewById(R.id.btn_message);
        llEdit = (LinearLayout) findViewById(R.id.ll_edit);
        btnMessage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                btnMessage.setVisibility(View.GONE);
                llEdit.setVisibility(View.VISIBLE);
                Utils.showSoftInput(etMessage,MessageChatActivity.this);
            }
        });
        etMessage = (EditText) findViewById(R.id.et_message);
        findViewById(R.id.btn_send).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String message=etMessage.getText().toString().trim();
                messageList.add(message);
//                etMessage.setText("");
                if(!TextUtils.isEmpty(message)){
                    adapter.notifyItemInserted(messageList.size());
                    recyclerView.smoothScrollToPosition(messageList.size());
                }
            }
        });

        recyclerView.setAdapter(adapter =new MyAdapter());
        adapter.notifyDataSetChanged();

        view.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                Rect rect = new Rect();
                view.getWindowVisibleDisplayFrame(rect);
                int rootInvisibleHeight = view.getHeight() - rect.bottom;
                Log.e("------", "onGlobalLayout:view.getHeight() "+view.getHeight() +"rect.bottom"+rect.bottom);
                if(rootInvisibleHeight==0){
                    if(lastRootInvisibleHeight!=0){
                        Log.e("隐藏", "onGlobalLayout: " );
                        //隐藏输入框和按钮
                        llEdit.setVisibility(View.INVISIBLE);
                        btnMessage.setVisibility(View.VISIBLE);
                    }
                }else{
                    Log.e("显示", "onGlobalLayout: " );
                }
                lastRootInvisibleHeight = rootInvisibleHeight;
            }
        });
    }

    public class MyAdapter extends RecyclerView.Adapter<MyAdapter.MyViewHolder>{

        @Override
        public MyAdapter.MyViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
            View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item,viewGroup,false);
            return new MyAdapter.MyViewHolder(view) ;
        }

        @Override
        public void onBindViewHolder(MyAdapter.MyViewHolder holder, int position) {
            holder.messageContent.setText(messageList.get(position));
        }

        @Override
        public int getItemCount() {
            return messageList.size();
        }

        class MyViewHolder extends RecyclerView.ViewHolder{

            public final TextView messageContent;

            public MyViewHolder(View itemView) {
                super(itemView);
                messageContent = (TextView) itemView.findViewById(R.id.message_content);
            }
        }
    }
}
