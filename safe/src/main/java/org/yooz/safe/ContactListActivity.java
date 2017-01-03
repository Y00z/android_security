package org.yooz.safe;

import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.SimpleAdapter;

import java.util.ArrayList;
import java.util.HashMap;

public class ContactListActivity extends ActionBarActivity {
	private ListView lv_concatc;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_contact);
		
		lv_concatc = (ListView) findViewById(R.id.lv_contact);
		
		findViewById(R.id.iv_item);

		
		lv_concatc.setAdapter(new SimpleAdapter(this, readContact(), R.layout.contact_list_item, 
				new String[]{"name","phone"}, new int[]{R.id.tv_name,R.id.tv_phone}));
		
		//触摸事件
		lv_concatc.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				String phone = readContact().get(position).get("phone");
				Intent intent = new Intent();
				
				intent.putExtra("phone", phone);
				setResult(RESULT_OK, intent);
				finish();
			}
		});
	}
	
	
	public ArrayList<HashMap<String, String>> readContact() {
		// 首先,从raw_contacts中读取联系人的id("contact_id")
		// 其次, 根据contact_id从data表中查询出相应的电话号码和联系人名称
		// 然后,根据mimetype来区分哪个是联系人,哪个是电话号码
		Uri rawContactUri = Uri
				.parse("content://com.android.contacts/raw_contacts");
		Uri dataUri = Uri.parse("content://com.android.contacts/data");
		ArrayList<HashMap<String, String>> list = new ArrayList<HashMap<String, String>>();
		// 1,首先,从raw_contacts中读取联系人的id("contact_id")
		Cursor rawContactCursor = getContentResolver().query(rawContactUri,
				new String[] { "contact_id" }, null, null, null);
		if (rawContactCursor != null) {
			while (rawContactCursor.moveToNext()) {
				String contactId = rawContactCursor.getString(rawContactCursor.getColumnIndex("contact_id"));
				// 2.其次, 根据contact_id从data表中查询出相应的电话号码和联系人名称
				Cursor dataCursor = getContentResolver().query(dataUri,
						new String[] { "data1", "mimetype" }, "raw_contact_id = ?",
						new String[] { contactId }, null);

				if (dataCursor != null) {
					HashMap<String, String> map = new HashMap<String, String>();
					while (dataCursor.moveToNext()) {
						String data1 = dataCursor.getString(0);
						String mimetype = dataCursor.getString(1);
						 System.out.println(contactId+":"+data1+":"+mimetype);
						// 3.然后,根据mimetype来区分哪个是联系人,哪个是电话号码
						if ("vnd.android.cursor.item/phone_v2".equals(mimetype)) {
							map.put("phone", data1);
						} else if ("vnd.android.cursor.item/name"
								.equals(mimetype)) {
							map.put("name", data1);
						}
					}

					list.add(map);
					dataCursor.close();
				}
			}
			rawContactCursor.close();
		}

		return list;
	}
}
