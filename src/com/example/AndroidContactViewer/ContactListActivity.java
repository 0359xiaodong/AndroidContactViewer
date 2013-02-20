package com.example.AndroidContactViewer;

import java.util.List;

import android.app.ListActivity;
import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.AndroidContactViewer.datastore.ContactDataSource;

public class ContactListActivity extends ListActivity {
    private ContactListActivity _activity = null;
    protected ContactAdapter contact_adapter;
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
        _activity = this;

		setContentView(R.layout.contact_list);
		ToolbarConfig toolbar = new ToolbarConfig(this, "Contacts");

		// setup the about button
		Button button = toolbar.getToolbarRightButton();
		button.setText("About");
		button.setOnClickListener(new View.OnClickListener() {
			public void onClick(View view) {
				Toast.makeText(
						ContactListActivity.this,
						"This is a sample application made for SENG 5199-1 in the MSSE program.",
						Toast.LENGTH_LONG).show();
			}
		});
		
		button = toolbar.getToolbarLeftButton();
		button.setText("Search");
		button.setOnClickListener(new View.OnClickListener() {
			public void onClick(View view) {
				onSearchRequested();
			}
		});
		

		// initialize the list view
		ContactDataSource datasource = new ContactDataSource(this);
		datasource.open();
        contact_adapter = new ContactAdapter(this, R.layout.contact_list_item, datasource.all());
		setListAdapter(contact_adapter);
		datasource.close();
		
		Intent intent = getIntent();
        String action = intent.getAction();
		if(action != null && action.equals(Intent.ACTION_SEARCH)) {
    		String query = intent.getStringExtra(SearchManager.QUERY);
    		((ContactAdapter)getListAdapter()).getFilter().filter(query);
        }
		
		ListView lv = getListView();
		lv.setTextFilterEnabled(true);
        lv.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long l) {
                _activity.closeContextMenu();
                return false;
            }
        });

        // setup context menu
        registerForContextMenu(lv);

        //Setup Search
        EditText search_box = (EditText)findViewById(R.id.search_box);
        search_box.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i2, int i3) {
                //To change body of implemented methods use File | Settings | File Templates.
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i2, int i3) {
                ContactListActivity.this.contact_adapter.getFilter().filter(charSequence);
            }

            @Override
            public void afterTextChanged(Editable editable) {
                //To change body of implemented methods use File | Settings | File Templates.
            }
        });
	}

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.action_menu, menu);
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
        switch (item.getItemId()) {
            case R.id.call:
                Toast.makeText(this, "Call", 5).show();
                return true;
            case R.id.message:
                Toast.makeText(this, "Message", 5).show();
                return true;
            case R.id.email:
                Toast.makeText(this, "email", 5).show();
                return true;
            case R.id.profile:
                Intent myIntent = new Intent(getBaseContext(), ContactViewActivity.class);
                myIntent.putExtra("ContactID", ((ContactAdapter)getListAdapter()).getItem(info.position).getContactId());
                startActivity(myIntent);
                return true;
            default:
                return super.onContextItemSelected(item);
        }
    }

    @Override
	protected void onListItemClick(ListView l, View v, int position, long id) {
		super.onListItemClick(l, v, position, id);

        this.openContextMenu(v);

	}

	/*
	 * We need to provide a custom adapter in order to use a custom list item
	 * view.
	 */
	private class ContactAdapter extends ArrayAdapter<Contact> {

		public ContactAdapter(Context context, int textViewResourceId,
				List<Contact> objects) {
			super(context, textViewResourceId, objects);
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			LayoutInflater inflater = getLayoutInflater();
			View item = inflater.inflate(R.layout.contact_list_item, parent, false);

			Contact contact = getItem(position);
			((TextView) item.findViewById(R.id.item_name)).setText(contact
					.getName());
			((TextView) item.findViewById(R.id.item_title)).setText(contact
					.getTitle());
			((TextView) item.findViewById(R.id.item_phone)).setText(contact
					.getDefaultContactPhone());

			return item;
		}
	}

}
