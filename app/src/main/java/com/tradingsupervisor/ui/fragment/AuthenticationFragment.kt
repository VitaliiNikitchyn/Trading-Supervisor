package com.tradingsupervisor.ui.fragment

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ProgressBar
import androidx.fragment.app.Fragment
import com.tradingsupervisor.R
import com.tradingsupervisor.ui.MainActivity

class AuthenticationFragment : Fragment() {
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_authentication, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        view.findViewById<View>(R.id.loginBtn).setOnClickListener { btn ->
            btn.isClickable = false
            val progressBar = view.findViewById<ProgressBar>(R.id.login_progressBar).apply {
                visibility = ProgressBar.VISIBLE
            }
            val username = view.findViewById<EditText>(R.id.username_editText).text.toString()
            val password = view.findViewById<EditText>(R.id.password_editText).text.toString()

            //-------------------FOR TEST purpose--------------------
            val activity = requireActivity()
            val sharedPref = requireActivity().getSharedPreferences(
                    getString(R.string.appSharedPreferences), Context.MODE_PRIVATE)
            val editor = sharedPref.edit()
            editor.putString(getString(R.string.authToken), "Bearer " + "fakeToken")
            editor.apply()
            val intent = Intent(activity, MainActivity::class.java)
            startActivity(intent)
            activity.finish()
            //--------------------------------------

            /*
                HttpClient.getApi().authenticate(username, password).enqueue(new Callback<AuthToken>() {
                    @Override
                    public void onResponse(Call<AuthToken> call, Response<AuthToken> response) {
                        btn.setClickable(true);
                        //check response code. May be 400 if login or password is bad
                        if (response.isSuccessful()) {
                            SharedPreferences sharedPref = getActivity().getSharedPreferences(
                                    getString(R.string.appSharedPreferences), Context.MODE_PRIVATE);
                            SharedPreferences.Editor editor = sharedPref.edit();
                            editor.putString(getString(R.string.authToken), "Bearer " + response.body().getToken());
                            editor.apply();

                            Intent intent = new Intent(getActivity(), MainActivity.class);
                            startActivity(intent);
                            getActivity().finish();
                        } else {
                            Toast.makeText(getActivity(), "Неправильний логін або пароль", Toast.LENGTH_SHORT).show();
                        }
                        progressBar.setVisibility(ProgressBar.GONE);
                    }

                    @Override
                    public void onFailure(Call<AuthToken> call, Throwable t) {
                        btn.setClickable(true);
                        if (t instanceof IOException) {
                            Toast.makeText(getActivity(), "Помилка авторизації. Перевірте з'єднання з інтернетом", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(getActivity(), "Помилка сервера. Зверніться до адміністраторів", Toast.LENGTH_SHORT).show();
                        }
                        progressBar.setVisibility(ProgressBar.GONE);
                    }
                });
                */
        }
    }

    companion object {
        const val TAG = "AuthenticationFragment"
        @JvmStatic
        fun newInstance(): AuthenticationFragment {
            return AuthenticationFragment()
        }
    }
}