package feezu.cn.myapplication;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.SurfaceView;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

	private TextSurfaceView tsfv;
	private ArrayList<String> list;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
//		setContentView(R.layout.activity_main);
//
//		tsfv = (TextSurfaceView) findViewById(R.id.tsfv);
//		tsfv.showText("我哈似的发射点发生嘎嘎说法发生发生发射点法速度发顺丰阿三地方 阿三地方阿萨法阿三地方阿三地方阿萨法\nasdfasfasfasdfasdfsdfasf");

		MScroll mScroll = new MScroll(this);

		mScroll.setFocusable(true);
		initList();
		setContentView(mScroll);
		mScroll.setText(list.get(0));
	}

	private void initList() {
		list = new ArrayList<>();
		list.add("一群孩子，从四五岁到十几岁不等，能有数十人，在村前的空地上迎着朝霞，正在哼哈有声的锻炼体魄。一张张稚嫩的小脸满是认真之sè，大一些的孩子虎虎生风，"+
				         "小一些的也比划的有模有样。" +
				        "一个肌体强健如虎豹的中年男子，穿着兽皮衣，皮肤呈古铜sè，黑发披散，炯炯有神的眼眸扫过每一个孩子，正在认真指点他们。\n" +
				         "\t\t“太阳初升，万物初始，生之气最盛，虽不能如传说中那般餐霞食气，但这样迎霞锻体自也有莫大好处，可充盈人体生机。一天之计在于晨，每rì早起多用功，强筋壮骨，活血炼筋，将来才能在这苍莽山脉中有活命的本钱。”站在前方、指点一群孩子的中年男子一脸严肃，认真告诫，而后又喝道：“你们明白吗？”\n" +
				         "\t\t“明白！”一群孩子中气十足，大声回应。" +
				        "\t山中多史前生物出没，时有遮蔽天空之巨翼横过，在地上投下大片的yīn影，亦有荒兽立于峰上，吞月而啸，更少不了各种毒虫伏行，异常可怖。\n" +
				         "\t\t“明白呀。”一个明显走神、慢了半拍的小家伙nǎi声nǎi气的叫道。" +
				         "\t这是一个很小的孩子，只有一两岁的样子，刚学会走路没几个月，也在跟着锻炼体魄。显然，他是自己凑过来的，混在了年长的孩子中，分明还不应该出现在这个队伍里。\n" +
				         "\t\t“哼哼哈嘿！”小家伙口中发声，嫩嫩的小手臂卖力的挥动着，效仿大孩子们的动作，可是他太过幼小，动作歪歪扭扭，且步履蹒跚，摇摇摆摆，再加上嘴角间残留的白sènǎi渍，引人发笑。list.add(\"\");\n" +
				        "  这是一个很小的孩子，只有一两岁的样子，刚学会走路没几个月，也在跟着锻炼体魄。显然，他是自己凑过来的，混在了年长的孩子中，分明还不应该出现在这个队伍里。\n" +
				         "    “哼哼哈嘿！”小家伙口中发声，嫩嫩的小手臂卖力的挥动着，效仿大孩子们的动作，可是他太过幼小，动作歪歪扭扭，且步履蹒跚，摇摇摆摆，再加上嘴角间残留的白sènǎi渍，引人发笑。\n" +
				         "    一群大孩子看着他，皆挤眉弄眼，让原本严肃的晨练气氛轻缓了不少。");
	}
}
