package book.chapter08.accesscontacts;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.provider.ContactsContract.CommonDataKinds.Phone;
import android.provider.ContactsContract.CommonDataKinds.StructuredName;
import android.provider.ContactsContract.Data;
import android.provider.ContactsContract.RawContacts;
import android.app.Activity;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.database.Cursor;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Toast;

public class MainActivity extends Activity {

	private EditText name,phone;
	private Button add,show;
	private ContentResolver resolver;
	private LinearLayout title;
	private ListView result;

	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		name=(EditText)this.findViewById(R.id.name);
		phone=(EditText)this.findViewById(R.id.phone);
		add=(Button)this.findViewById(R.id.add);
		show=(Button)this.findViewById(R.id.show);
		result = (ListView) findViewById(R.id.result);
		title=(LinearLayout)findViewById(R.id.title);

		title.setVisibility(View.INVISIBLE);
		resolver = getContentResolver();
		MyOnClickListener myOnClickListener=new MyOnClickListener();//创建事件监听器
		//注册事件监听器
		add.setOnClickListener(myOnClickListener);
		show.setOnClickListener(myOnClickListener);
	}
	
	//内部私有类
	private class MyOnClickListener implements OnClickListener{

		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			switch(v.getId()){
			case R.id.add:
				addPerson();
				break;
			case R.id.show:
				title.setVisibility(View.VISIBLE);
				ArrayList<Map<String,String>>persons=queryPerson();
			SimpleAdapter adapter=new SimpleAdapter(MainActivity.this,persons,R.layout.result,new String[]{"id",
					"name","num"},new int[]{R.id.personid,R.id.personname,R.id.personnum});
			result.setAdapter(adapter);
			    break;
			default:break;
				
				
			}
		}
		
	}
	
	//添加联系人
	public void addPerson(){
		String nameStr=name.getText().toString();//获取联系人姓名
		String numStr=phone.getText().toString();//获取联系人号码
		ContentValues values=new ContentValues();//创建一个空的ContentValues
		//向RawContacts.CONTENT_URL插入空值，目的是获取返回的ID号
		Uri rawContactUri=resolver.insert(RawContacts.CONTENT_URI,values);
		long contactId=ContentUris.parseId(rawContactUri);//得到新联系人的ID号
		System.out.println(contactId);
		values.clear();//清空values的内容
		values.put(Data.RAW_CONTACT_ID, contactId);//设置ID号
		values.put(Data.MIMETYPE,StructuredName.CONTENT_ITEM_TYPE);//设置类型
		values.put(StructuredName.GIVEN_NAME, nameStr);//设置姓名
		resolver.insert(android.provider.ContactsContract.Data.CONTENT_URI, values);//向联系人URI中添加联系人名字
		values.clear();
		values.put(Data.RAW_CONTACT_ID,contactId);//设置ID号
		values.put(Data.MIMETYPE, Phone.CONTENT_ITEM_TYPE);//设置类型
		values.put(Phone.NUMBER,numStr);//设置ID号
		values.put(Data.RAW_CONTACT_ID,contactId);//设置ID号
		resolver.insert(android.provider.ContactsContract.Data.CONTENT_URI,values);// 向联系人电话号码URI添加电话号码
		Toast.makeText(MainActivity.this, "联系人数据添加成功！", 1000).show();

	}
	
	//创建一个保存所有联系人信息的列表，每项是一个map对象
	public ArrayList<Map<String,String>>queryPerson(){
		ArrayList<Map<String,String>>detail=new ArrayList<Map<String,String>>();
		Cursor cursor=resolver.query(ContactsContract.Contacts.CONTENT_URI, null, null, null, null);//查询通讯录中所有联系人
		
		//循环遍历每一个联系人
		while(cursor.moveToNext()){
			Map<String,String>person=new HashMap<String,String>();//每个联系人信息用一个map对象存储
			//获取联系人ID号
			String personId=cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts._ID));
			//获取联系人姓名
			String name=cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));
			//将获取到的信息存入map对象中
			person.put("id", personId);
			person.put("name", name);
			//根据ID号查询手机号码
			Cursor nums=resolver.query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI,null, 
					ContactsContract.CommonDataKinds.Phone.CONTACT_ID+"="+personId, null, null);
			if(nums.moveToNext()){
				String num=nums.getString(nums.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
				person.put("num", num);//将手机号存入map对象中
			}
			nums.close();//关闭资源
			detail.add(person);
		}
		cursor.close();//关闭资源
		System.out.println(detail);
		return detail;//返回查询列表
		
	}

}
