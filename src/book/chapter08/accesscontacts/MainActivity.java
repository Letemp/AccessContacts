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
		MyOnClickListener myOnClickListener=new MyOnClickListener();//�����¼�������
		//ע���¼�������
		add.setOnClickListener(myOnClickListener);
		show.setOnClickListener(myOnClickListener);
	}
	
	//�ڲ�˽����
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
	
	//�����ϵ��
	public void addPerson(){
		String nameStr=name.getText().toString();//��ȡ��ϵ������
		String numStr=phone.getText().toString();//��ȡ��ϵ�˺���
		ContentValues values=new ContentValues();//����һ���յ�ContentValues
		//��RawContacts.CONTENT_URL�����ֵ��Ŀ���ǻ�ȡ���ص�ID��
		Uri rawContactUri=resolver.insert(RawContacts.CONTENT_URI,values);
		long contactId=ContentUris.parseId(rawContactUri);//�õ�����ϵ�˵�ID��
		System.out.println(contactId);
		values.clear();//���values������
		values.put(Data.RAW_CONTACT_ID, contactId);//����ID��
		values.put(Data.MIMETYPE,StructuredName.CONTENT_ITEM_TYPE);//��������
		values.put(StructuredName.GIVEN_NAME, nameStr);//��������
		resolver.insert(android.provider.ContactsContract.Data.CONTENT_URI, values);//����ϵ��URI�������ϵ������
		values.clear();
		values.put(Data.RAW_CONTACT_ID,contactId);//����ID��
		values.put(Data.MIMETYPE, Phone.CONTENT_ITEM_TYPE);//��������
		values.put(Phone.NUMBER,numStr);//����ID��
		values.put(Data.RAW_CONTACT_ID,contactId);//����ID��
		resolver.insert(android.provider.ContactsContract.Data.CONTENT_URI,values);// ����ϵ�˵绰����URI��ӵ绰����
		Toast.makeText(MainActivity.this, "��ϵ��������ӳɹ���", 1000).show();

	}
	
	//����һ������������ϵ����Ϣ���б�ÿ����һ��map����
	public ArrayList<Map<String,String>>queryPerson(){
		ArrayList<Map<String,String>>detail=new ArrayList<Map<String,String>>();
		Cursor cursor=resolver.query(ContactsContract.Contacts.CONTENT_URI, null, null, null, null);//��ѯͨѶ¼��������ϵ��
		
		//ѭ������ÿһ����ϵ��
		while(cursor.moveToNext()){
			Map<String,String>person=new HashMap<String,String>();//ÿ����ϵ����Ϣ��һ��map����洢
			//��ȡ��ϵ��ID��
			String personId=cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts._ID));
			//��ȡ��ϵ������
			String name=cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));
			//����ȡ������Ϣ����map������
			person.put("id", personId);
			person.put("name", name);
			//����ID�Ų�ѯ�ֻ�����
			Cursor nums=resolver.query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI,null, 
					ContactsContract.CommonDataKinds.Phone.CONTACT_ID+"="+personId, null, null);
			if(nums.moveToNext()){
				String num=nums.getString(nums.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
				person.put("num", num);//���ֻ��Ŵ���map������
			}
			nums.close();//�ر���Դ
			detail.add(person);
		}
		cursor.close();//�ر���Դ
		System.out.println(detail);
		return detail;//���ز�ѯ�б�
		
	}

}
