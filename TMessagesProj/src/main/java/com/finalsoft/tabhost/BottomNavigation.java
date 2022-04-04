package com.finalsoft.tabhost;

//import com.luseen.spacenavigation.SpaceItem;
//import com.luseen.spacenavigation.SpaceNavigationView;
//import com.luseen.spacenavigation.SpaceOnClickListener;


public class BottomNavigation {
/*  private SpaceNavigationView spaceNavigationView;
  Context context;

  public BottomNavigation(Context context) {

    View v = View.inflate(context,R.layout.tab,null) ;//new SpaceNavigationView(context,null,0);
    spaceNavigationView = v.findViewById(R.id.bottom_navigation);
    spaceNavigationView.setLayoutParams(
        new LinearLayout.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT,
            Gravity.BOTTOM

        ));
    spaceNavigationView.setGravity(Gravity.BOTTOM);
    spaceNavigationView.setCentreButtonIcon(R.drawable.msg_home);
    spaceNavigationView.setActiveSpaceItemColor(
        context.getResources().getColor(R.color.badge_background_color));
    this.context = context;
  }
  //region Bottom Nav Methods

  public SpaceNavigationView getSpaceNavigationView(Bundle savedInstanceState) {

    if (savedInstanceState != null) {
      spaceNavigationView.initWithSaveInstanceState(savedInstanceState);
    }
    //spaceNavigationView.changeCenterButtonIcon(R.drawable.ic_account);

    spaceNavigationView.addSpaceItem(new SpaceItem("a",R.drawable.book_bot));
    spaceNavigationView.addSpaceItem(new SpaceItem("b",R.drawable.book_channel));
    spaceNavigationView.addSpaceItem(new SpaceItem("c",R.drawable.book_group));
    spaceNavigationView.addSpaceItem(new SpaceItem("d",R.drawable.book_user));

    spaceNavigationView.setCentreButtonColor(
        context.getResources().getColor(R.color.badge_background_color));
    spaceNavigationView.setSpaceItemTextSize(
        (int) context.getResources().getDimension(R.dimen.main_content_height));
    spaceNavigationView.changeCurrentItem(-1);

    spaceNavigationView.setSpaceOnClickListener(new SpaceOnClickListener() {
      @Override
      public void onCentreButtonClick() {

      }

      @Override
      public void onItemClick(int itemIndex, String itemName) {
        bottomNavigation(itemIndex);
      }

      @Override
      public void onItemReselected(int itemIndex, String itemName) {
        bottomNavigation(itemIndex);
      }
    });

*//*    spaceNavigationView.shouldShowFullBadgeText(true);
    //spaceNavigationView.showBadgeAtIndex(, 0, Color.RED);
    spaceNavigationView.showBadgeAtIndex(1, 0, Color.RED);
    spaceNavigationView.showBadgeAtIndex(2, 0, Color.RED);
    spaceNavigationView.showBadgeAtIndex(3, 0, Color.RED);*//*
    //badgeCenter();
    return spaceNavigationView;
  }

  public void bottomNavigation(int itemIndex) {
    switch (itemIndex) {
      case 0: {

        break;
      }
      case 1: {

        break;
      }
      case 2: {
        break;
      }
      case 3: {

        break;
      }
    }
  }

  public void hideBottomNavigation() {
    if (spaceNavigationView != null) {
      spaceNavigationView.setVisibility(View.GONE);
    }
  }

  public void showBottomNavigation() {
    if (spaceNavigationView != null) {
      spaceNavigationView.setVisibility(View.VISIBLE);
    }
  }*/
  //endregion
}
