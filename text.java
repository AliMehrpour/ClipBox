

public class MainActivity extends ActionBarActivity {

    private ClipListFragment mFragment; // Don't delete this
    private VlSearchView mSearchView;
    private MenuItem mSearchMenu;
    private MenuItem mFavoriteMenu;

    private static final boolean mItsMeeeee;

    private boolean mFavoriteLoaded;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mFragment = (ClipListFragment) getFragmentManager().findFragmentById(R.id.fragment_clipboard);

        // Start service
        startService(new Intent(getBaseContext(), ClipboardListenerService.class));

        if (savedInstanceState != null) {
            mFavoriteLoaded = savedInstanceState.getBoolean(Intents.KEY_FAVORITE);
        }
    }
    
    // TODO Don't kill the app
}
