package idv.ron.logindemo_android;


import android.app.Activity;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.JsonObject;

public class MainFragment extends Fragment {
    private final static String TAG = "TAG_MainFragment";
    private Activity activity;
    private EditText etName, etPassword;
    private Button btLogin,btSignUp;
    private TextView textView;
    private Gson gson;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activity = getActivity();
        gson = new Gson();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        activity.setTitle(R.string.textLogin);
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_main, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        etName = view.findViewById(R.id.etName);
        etPassword = view.findViewById(R.id.etPassword);
        btLogin = view.findViewById(R.id.btLogin);
        textView = view.findViewById(R.id.textView);
        btSignUp = view.findViewById(R.id.btSignUp);

        btSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Navigation.findNavController(btSignUp).navigate(R.id.action_mainFragment_to_lalalalaFragment);
            }
        });

        btLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String userName = etName.getText().toString();
                String password = etPassword.getText().toString();

                User user = new User(0,userName, password,null);
                if (userName.length() <= 0 || password.length() <= 0) {
                    textView.setText(R.string.textInputToSomethingIdiot);
                    return;
                }

                if (Common.networkConnected(activity)) {
                    String url = Common.URL + "LoginServlet";
                    String outStr = gson.toJson(user);
                    CommonTask loginTask = new CommonTask(url, outStr);

                    boolean isUserValid = false;
                    try {
                        JsonObject jsonObject = new JsonObject();
                        jsonObject.addProperty("action", "findById");
                        String jsonIn = loginTask.execute().get();
                        jsonObject = gson.fromJson(jsonIn, JsonObject.class);
                        isUserValid = jsonObject.get("findById").getAsBoolean();
                    } catch (Exception e) {
                        Log.e(TAG, e.getMessage());
                    }

                    if (isUserValid) {
                        Bundle bundle = new Bundle();
                        bundle.putSerializable("user", user);
                        Navigation.findNavController(textView)
                                .navigate(R.id.action_mainFragment_to_resultFragment, bundle);
                    } else {
                        textView.setText(R.string.textLoginFail);
                    }
                }
            }
        });

    }
}
