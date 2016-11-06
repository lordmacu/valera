package co.cristiangarcia.bibliareinavalera.activity;

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.TabLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.view.Menu;
import android.view.MenuItem;
import android.view.SubMenu;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;
import co.cristiangarcia.bibliareinavalera.R;

import co.cristiangarcia.bibliareinavalera.BuildConfig;
import co.cristiangarcia.bibliareinavalera.audiodown.AudioActivity;
import co.cristiangarcia.bibliareinavalera.bd.DatabaseHelper;
import co.cristiangarcia.bibliareinavalera.fragment.CapituloFragment;
import co.cristiangarcia.bibliareinavalera.fragment.FrecListFragment;
import co.cristiangarcia.bibliareinavalera.fragment.HomeFragment;
import co.cristiangarcia.bibliareinavalera.node.Libro;
import co.cristiangarcia.bibliareinavalera.service.PlayService;
import co.cristiangarcia.bibliareinavalera.service.PlayService.AudioServiceBinder;
import co.cristiangarcia.bibliareinavalera.util.LibrosHelper;
import co.cristiangarcia.bibliareinavalera.util.Preference;
import cz.msebera.android.httpclient.HttpStatus;
import cz.msebera.android.httpclient.impl.client.cache.CacheConfig;

public class LibroActivity extends co.cristiangarcia.bibliareinavalera.activity.BaseActivity implements ServiceConnection {
    public static final String SHARE_LAST_CAPITULO = "last_capitulo";
    public static final String SHARE_LAST_LIBRO = "last_libro";
    private ViewPagerAdapter adapter;
    private BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            int play = intent.getIntExtra("play", -1);
            int duration = intent.getIntExtra("duration", -1);
            boolean initPlay = intent.getBooleanExtra("initPlay", false);
            boolean donePlay = intent.getBooleanExtra("donePlay", false);
            String title = intent.getStringExtra("title");
            if (duration > 0) {
                LibroActivity.this.playProgressBar.setMax(duration);
            }
            if (play > 0) {
                LibroActivity.this.playProgressBar.setProgress(play);
                LibroActivity.this.playProgressBar.setSecondaryProgress(0);
                int seconds = (play / CacheConfig.DEFAULT_MAX_CACHE_ENTRIES) % 60;
                int minutes = (play / 60000) % 60;
                int mseconds = (duration / CacheConfig.DEFAULT_MAX_CACHE_ENTRIES) % 60;
                int mminutes = (duration / 60000) % 60;
                LibroActivity.this.timeAudioView.setText((minutes < 10 ? "0" + minutes : Integer.valueOf(minutes)) + ":" + (seconds < 10 ? "0" + seconds : Integer.valueOf(seconds)) + " / " + (mminutes < 10 ? "0" + mminutes : Integer.valueOf(mminutes)) + ":" + (mseconds < 10 ? "0" + mseconds : Integer.valueOf(mseconds)));
            }
            if (donePlay) {
                LibroActivity.this.playProgressBar.setProgress(0);
                LibroActivity.this.playImage.setImageResource(R.mipmap.ic_play_white_24dp);
                LibroActivity.this.timeAudioView.setText(BuildConfig.FLAVOR);
            }
            if (initPlay) {
                LibroActivity.this.playImage.setImageResource(R.mipmap.ic_pause_white_24dp);
            }
            if (title != null) {
                LibroActivity.this.nameAudioView.setText(title);
            }
        }
    };
    private int capitulo;
    private CoordinatorLayout coordinator;
    private View descargarButton;
    private int libro;
    private int librocaps;
    private String libroname;
    private TextView nameAudioView;
    private View nextButton;
    private View playButton;
    private ImageView playImage;
    private SeekBar playProgressBar;
    private AudioServiceBinder playService = null;
    private View prevButton;
    private View stopButton;
    private TabLayout tabLayout;
    private TextView timeAudioView;
    private int versiculo;
    private ViewPager viewPager;
    private View viewPlay;

    class ViewPagerAdapter extends FragmentPagerAdapter {
        private final List<CapituloFragment> mFragmentList = new ArrayList();
        private final List<String> mFragmentTitleList = new ArrayList();

        public ViewPagerAdapter(FragmentManager manager) {
            super(manager);
        }

        public CapituloFragment getItem(int position) {
            return (CapituloFragment) this.mFragmentList.get(position);
        }

        public int getCount() {
            return this.mFragmentList.size();
        }

        public void addFragment(CapituloFragment fragment, String title) {
            this.mFragmentList.add(fragment);
            this.mFragmentTitleList.add(title);
        }

        public CharSequence getPageTitle(int position) {
            return (CharSequence) this.mFragmentTitleList.get(position);
        }
    }

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState, R.layout.libro_main);
        if (PreferenceManager.getDefaultSharedPreferences(getApplicationContext()).getBoolean("check_preference", false)) {
            getWindow().addFlags(128);
        }
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            this.libro = extras.getInt("libro");
            this.capitulo = extras.getInt("capitulo");
            this.versiculo = extras.getInt("versiculo");
            Libro libro = LibrosHelper.getLibro(this.libro);
            if (libro != null) {
                this.libroname = libro.getName();
                this.librocaps = libro.getNumCap();
                setTitle(this.libroname);
            }
        }
        this.coordinator = (CoordinatorLayout) findViewById(R.id.libro_coordinator_layout);
        this.viewPager = (ViewPager) findViewById(R.id.viewpager);
        this.viewPager.setOffscreenPageLimit(2);
        setupViewPager(this.viewPager);
        this.tabLayout = (TabLayout) findViewById(R.id.tabs);
        this.tabLayout.setupWithViewPager(this.viewPager);
        this.viewPlay = findViewById(R.id.libro_m_play);
        this.nameAudioView = (TextView) findViewById(R.id.libro_m_tv1);
        this.timeAudioView = (TextView) findViewById(R.id.libro_m_tv2);
        this.playProgressBar = (SeekBar) findViewById(R.id.libro_m_sb1);
        this.playButton = findViewById(R.id.libro_m_bplay);
        this.playImage = (ImageView) findViewById(R.id.libro_m_iplay);
        this.stopButton = findViewById(R.id.libro_m_bstop);
        this.nextButton = findViewById(R.id.libro_m_bnext);
        this.prevButton = findViewById(R.id.libro_m_bprev);
        this.descargarButton = findViewById(R.id.libro_m_bdown);
        this.playProgressBar.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
            }

            public void onStartTrackingTouch(SeekBar seekBar) {
                if (LibroActivity.this.playService != null) {
                    LibroActivity.this.playService.pause();
                }
            }

            public void onStopTrackingTouch(SeekBar seekBar) {
                if (LibroActivity.this.playService != null && LibroActivity.this.playService.isOpen()) {
                    LibroActivity.this.playImage.setImageResource(R.mipmap.ic_pause_white_24dp);
                    LibroActivity.this.playService.setCurrentPosition(seekBar.getProgress());
                }
            }
        });
        this.playButton.setOnClickListener(new OnClickListener() {
            public void onClick(View arg0) {
                if (LibroActivity.this.playService != null) {
                    if (LibroActivity.this.playService.isPlaying()) {
                        LibroActivity.this.playService.pause();
                        LibroActivity.this.playImage.setImageResource(R.mipmap.ic_play_white_24dp);
                        return;
                    }
                    if (LibroActivity.this.playService.isPause()) {
                        LibroActivity.this.playService.resume();
                    } else {
                        LibroActivity.this.playService.setplay(LibroActivity.this.libroname, LibroActivity.this.libro, LibroActivity.this.viewPager.getCurrentItem() + 1, LibroActivity.this.librocaps);
                        LibroActivity.this.playService.play();
                    }
                    if (LibroActivity.this.playService.isPlaying()) {
                        LibroActivity.this.playImage.setImageResource(R.mipmap.ic_pause_white_24dp);
                    } else {
                        LibroActivity.this.playImage.setImageResource(R.mipmap.ic_play_white_24dp);
                    }
                }
            }
        });
        this.stopButton.setOnClickListener(new OnClickListener() {
            public void onClick(View arg0) {
                if (LibroActivity.this.playService != null) {
                    LibroActivity.this.playService.stop();
                    LibroActivity.this.playImage.setImageResource(R.mipmap.ic_play_white_24dp);
                    LibroActivity.this.nameAudioView.setText(BuildConfig.FLAVOR);
                    LibroActivity.this.timeAudioView.setText(BuildConfig.FLAVOR);
                    LibroActivity.this.playProgressBar.setProgress(0);
                }
            }
        });
        this.nextButton.setOnClickListener(new OnClickListener() {
            public void onClick(View arg0) {
                if (LibroActivity.this.playService != null && !LibroActivity.this.nameAudioView.getText().equals(BuildConfig.FLAVOR)) {
                    LibroActivity.this.playService.next();
                }
            }
        });
        this.nextButton.setOnLongClickListener(new OnLongClickListener() {
            public boolean onLongClick(View arg0) {
                Toast.makeText(LibroActivity.this.getApplicationContext(), "Siguiente Audio", 0).show();
                return true;
            }
        });
        this.prevButton.setOnClickListener(new OnClickListener() {
            public void onClick(View arg0) {
                if (LibroActivity.this.playService != null && !LibroActivity.this.nameAudioView.getText().equals(BuildConfig.FLAVOR)) {
                    LibroActivity.this.playService.prev();
                }
            }
        });
        this.prevButton.setOnLongClickListener(new OnLongClickListener() {
            public boolean onLongClick(View arg0) {
                Toast.makeText(LibroActivity.this.getApplicationContext(), "Anterior Audio", 0).show();
                return true;
            }
        });
        this.descargarButton.setOnClickListener(new OnClickListener() {
            public void onClick(View arg0) {
                Intent myIntent = new Intent();
                myIntent.setClass(LibroActivity.this.getApplicationContext(), AudioActivity.class);
                myIntent.setFlags(268435456);
                LibroActivity.this.startActivity(myIntent);
            }
        });
        this.descargarButton.setOnLongClickListener(new OnLongClickListener() {
            public boolean onLongClick(View arg0) {
                Toast.makeText(LibroActivity.this.getApplicationContext(), "Descargar Audio", 0).show();
                return true;
            }
        });
        if (savedInstanceState != null) {
            this.viewPager.onRestoreInstanceState(savedInstanceState.getParcelable("vp"));
        } else {
            this.viewPager.setCurrentItem(this.capitulo - 1);
        }
        DatabaseHelper.getLtHelper(this).addLibrosFrecuentes(this.libro,getBaseContext());
        sendBroadcast(new Intent(FrecListFragment.action_frecuente));
        startService(new Intent(this, PlayService.class));
        bindService(new Intent(this, PlayService.class), this, 1);
    }

    public void onServiceConnected(ComponentName name, IBinder service) {
        if (service instanceof AudioServiceBinder) {
            this.playService = (AudioServiceBinder) service;
            if (this.playService.isOpen()) {
                this.playService.sendTitle();
                if (this.playService.isPlaying()) {
                    this.playImage.setImageResource(R.mipmap.ic_pause_white_24dp);
                    this.viewPlay.setVisibility(0);
                    return;
                }
                this.playService.sendPlay();
            }
        }
    }

    public void onServiceDisconnected(ComponentName name) {
        this.playService = null;
    }

    public void onResume() {
        super.onResume();
        registerReceiver(this.broadcastReceiver, new IntentFilter(PlayService.action_progress));
    }

    public void onPause() {
        super.onPause();
        Preference.putInt(this, SHARE_LAST_LIBRO, this.libro);
        Preference.putInt(this, SHARE_LAST_CAPITULO, this.viewPager.getCurrentItem() + 1);
        sendBroadcast(new Intent(HomeFragment.action_up_ultlec));
        unregisterReceiver(this.broadcastReceiver);
    }

    public void onDestroy() {
        super.onDestroy();
        if (this.playService != null) {
            unbindService(this);
        }
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_act_libro, menu);
        SubMenu subMenu1 = menu.getItem(0).getSubMenu();
        for (int i = 0; i < this.librocaps; i++) {
            subMenu1.add(0, i + 100, 0, "Cap\u00edtulo " + (i + 1));
        }
        return super.onCreateOptionsMenu(menu);
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.menu_audio) {
            switchViewPlay();
            return true;
        } else if (item.getItemId() == R.id.menu_marcador) {
            int accion = DatabaseHelper.getLtHelper(this).addDelMarcador(this.libro, this.viewPager.getCurrentItem() + 1,getBaseContext());
            if (accion > 0) {
                Toast.makeText(this, this.libroname + " " + (this.viewPager.getCurrentItem() + 1) + " " + (accion == 1 ? "recordar" : "olvidar"), 1).show();
            }
            sendBroadcast(new Intent(HomeFragment.action_up_ultlec));
            return true;
        } else if (item.getItemId() < 100 || item.getItemId() > HttpStatus.SC_MULTIPLE_CHOICES) {
            return super.onOptionsItemSelected(item);
        } else {
            this.viewPager.setCurrentItem(item.getItemId() - 100);
            return true;
        }
    }

    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 1101 && grantResults.length > 0 && grantResults[0] == 0) {
            switchViewPlay();
        }
    }

    public void switchViewPlay() {
        if (ContextCompat.checkSelfPermission(this, "android.permission.WRITE_EXTERNAL_STORAGE") != 0) {
            Toast.makeText(this, "Es necesario aceptar el permiso para utilizar los audios", 1).show();
            ActivityCompat.requestPermissions(this, new String[]{"android.permission.WRITE_EXTERNAL_STORAGE"}, 1101);
        } else if (this.viewPlay.getVisibility() == 8) {
            this.viewPlay.setVisibility(0);
        } else {
            this.viewPlay.setVisibility(8);
        }
    }

    private void setupViewPager(ViewPager viewPager) {
        this.adapter = new ViewPagerAdapter(getSupportFragmentManager());
        for (int i = 1; i <= this.librocaps; i++) {
            int i2;
            ViewPagerAdapter viewPagerAdapter = this.adapter;
            int i3 = this.libro;
            if (this.capitulo == i) {
                i2 = this.versiculo;
            } else {
                i2 = 0;
            }
            viewPagerAdapter.addFragment(CapituloFragment.newInstance(i3, i, i2), getString(R.string.capitulo_n, new Object[]{Integer.valueOf(i)}));
        }
        viewPager.setAdapter(this.adapter);
    }
}
