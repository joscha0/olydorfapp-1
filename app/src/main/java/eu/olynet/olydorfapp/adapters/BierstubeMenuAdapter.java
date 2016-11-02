/*
 * This file is part of OlydorfApp.
 *
 * OlydorfApp is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * OlydorfApp is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with OlydorfApp.  If not, see <http://www.gnu.org/licenses/>.
 */
package eu.olynet.olydorfapp.adapters;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import org.apache.commons.lang3.NotImplementedException;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Locale;

import eu.olynet.olydorfapp.R;
import eu.olynet.olydorfapp.activities.DrinkViewerActivity;
import eu.olynet.olydorfapp.activities.FoodViewerActivity;
import eu.olynet.olydorfapp.activities.MealOfTheDayViewerActivity;
import eu.olynet.olydorfapp.fragments.DrinkViewerFragment;
import eu.olynet.olydorfapp.fragments.FoodViewerFragment;
import eu.olynet.olydorfapp.fragments.MealOfTheDayViewerFragment;
import eu.olynet.olydorfapp.model.AbstractMetaItem;
import eu.olynet.olydorfapp.model.CategoryItem;
import eu.olynet.olydorfapp.model.DailyMealItem;
import eu.olynet.olydorfapp.model.DrinkItem;
import eu.olynet.olydorfapp.model.DrinkSizeItem;
import eu.olynet.olydorfapp.model.FoodItem;
import eu.olynet.olydorfapp.model.MealOfTheDayItem;
import eu.olynet.olydorfapp.utils.UtilsDevice;
import eu.olynet.olydorfapp.utils.UtilsMiscellaneous;

/**
 * @author <a href="mailto:simon.domke@olynet.eu">Simon Domke</a>
 */
public class BierstubeMenuAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final int HEADLINE_TYPE = 0;
    private static final int DAILY_MEAL_TYPE = 1;
    private static final int DAILY_DRINK_TYPE = 2;
    private static final int FOOD_TYPE = 3;
    private static final int DRINK_TYPE = 4;

    private MealOfTheDayItem mealOfTheDayItem;
    private DailyMealItem dailyMealItem;
    private List<FoodItem> foodItems;
    private List<DrinkItem> drinkItems;
    private List<CategoryItem> categoryItems;

    private int headlineSpecial = -1;
    private int startSpecial = -1;
    private int headlineFood = -1;
    private int startFood = -1;
    private int headlineDrink = -1;
    private int startDrink = -1;

    private final Context context;

    /**
     * @param context the Context.
     */
    public BierstubeMenuAdapter(Context context) {
        this.context = context;
        setData(null, null, new ArrayList<>(), new ArrayList<>(), new ArrayList<>());
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view;
        switch (viewType) {
            case HEADLINE_TYPE:
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_headline,
                                                                        parent, false);
                return new HeadlineHolder(view);
            case DAILY_MEAL_TYPE:
                view = LayoutInflater.from(parent.getContext())
                                     .inflate(R.layout.card_meal_of_the_day, parent,
                                              false);
                return new DailyMealHolder(view);
            case DAILY_DRINK_TYPE:
                throw new NotImplementedException("not yet implemented");
            case FOOD_TYPE:
                view = LayoutInflater.from(parent.getContext())
                                     .inflate(R.layout.card_food, parent, false);
                return new FoodHolder(view);
            case DRINK_TYPE:
                view = LayoutInflater.from(parent.getContext())
                                     .inflate(R.layout.card_drink, parent, false);
                return new DrinkHolder(view);
            default:
                throw new RuntimeException("unknown item view type");
        }
    }

    @Override
    public int getItemViewType(int position) {
        if (position == headlineSpecial) {
            return HEADLINE_TYPE;
        } else if (position == startSpecial && mealOfTheDayItem != null) {
            return DAILY_MEAL_TYPE;
        } else if (position == headlineFood) {
            return HEADLINE_TYPE;
        } else if (position >= startFood && position < headlineDrink) {
            return FOOD_TYPE;
        } else if (position == headlineDrink) {
            return HEADLINE_TYPE;
        } else if (position >= startDrink) {
            return DRINK_TYPE;
        } else {
            throw new RuntimeException("unexpected position " + position);
        }
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        switch (getItemViewType(position)) {
            case HEADLINE_TYPE:
                int resID;
                if (position == headlineSpecial) {
                    resID = R.string.bierstube_menu_headline_daily;
                } else if (position == headlineFood) {
                    resID = R.string.bierstube_menu_headline_foods;
                } else if (position == headlineDrink) {
                    resID = R.string.bierstube_menu_headline_drinks;
                } else {
                    throw new RuntimeException("something went wrong during type determination");
                }
                ((HeadlineHolder) holder).vTitle.setText(resID);
                break;
            case DAILY_MEAL_TYPE:
                bindDailyMealHolder((DailyMealHolder) holder);
                break;
            case DAILY_DRINK_TYPE:
                throw new NotImplementedException("not yet implemented");
            case FOOD_TYPE:
                bindFoodHolder((FoodHolder) holder, position - startFood);
                break;
            case DRINK_TYPE:
                int n = startDrink;
                int pos = 0;
                int size = 0;
                for (DrinkItem drinkItem : drinkItems) {
                    int prevN = n;
                    n += drinkItem.getDrinkSizes().size();
                    if (n > position) {
                        size = position - prevN;
                        break;
                    }
                    pos++;
                }
                bindDrinkHolder((DrinkHolder) holder, pos, size);
                break;
            default:
                throw new RuntimeException("unknown item view type");
        }
    }

    /**
     * @return the number of drink cards present.
     */
    private int numberOfDrinkCards() {
        int n = 0;
        for (DrinkItem drinkItem : drinkItems) {
            n += drinkItem.getDrinkSizes().size();
        }
        return n;
    }

    /**
     * Fills the ViewHolder with the information about today's special meal.
     *
     * @param holder the ViewHolder to be filled.
     */
    private void bindDailyMealHolder(DailyMealHolder holder) {
        if (mealOfTheDayItem == null || dailyMealItem == null) {
            return;
        }

        /* set the correct mealOfTheDayItem in the ViewHolder for the OnClickListener */
        holder.mealOfTheDayItem = mealOfTheDayItem;
        holder.dailyMealItem = dailyMealItem;

        /* Headline */
        Calendar cal = new GregorianCalendar();
        cal.setTime(mealOfTheDayItem.getDate());
        holder.vHeadline.setText("Tagesessen (" + cal.get(Calendar.DAY_OF_MONTH) + ". " +
                                 cal.getDisplayName(Calendar.MONTH, Calendar.LONG,
                                                    Locale.getDefault()) + ")");

        /* Icon */
        holder.vIcon.setImageResource(
                dailyMealItem.isVegetarian() ? R.drawable.carrot
                                             : R.drawable.meat);

        /* Name */
        holder.vName.setText(dailyMealItem.getName());

        /* Cook */
        holder.vCook.setText(mealOfTheDayItem.getCook());

        /* Price */
        NumberFormat deDE = NumberFormat.getCurrencyInstance(Locale.GERMANY);
        holder.vPrice.setText(deDE.format(mealOfTheDayItem.getPrice()));

        /* Image */
        byte[] image = mealOfTheDayItem.getImage();
        int screenWidth = UtilsDevice.getScreenWidth(context);
        Bitmap bitmap = UtilsMiscellaneous.getOptimallyScaledBitmap(image, screenWidth);
        if (bitmap == null) { /* fallback to DailyMeal image */
            image = dailyMealItem.getImage();
            bitmap = UtilsMiscellaneous.getOptimallyScaledBitmap(image, screenWidth);
        }
        if (bitmap != null) {
            holder.vImage.setImageBitmap(bitmap);
        } else {
            holder.vImage.setImageResource(R.drawable.ic_account_circle_white_64dp);
        }
    }

    /**
     * Fills the ViewHolder with the information about the food.
     *
     * @param holder the ViewHolder to be filled.
     */
    private void bindFoodHolder(FoodHolder holder, int pos) {
        if (foodItems.size() < pos) {
            return;
        }

        holder.foodItem = foodItems.get(pos);

        /* Name */
        holder.vName.setText(holder.foodItem.getName());

        /* Icon */
        holder.vIcon.setImageResource(holder.foodItem.isVegetarian() ? R.drawable.carrot
                                                                     : R.drawable.meat);

        /* Price */
        NumberFormat deDE = NumberFormat.getCurrencyInstance(Locale.GERMANY);
        holder.vPrice.setText(deDE.format(holder.foodItem.getPrice()));

        /* Image */
        byte[] image = holder.foodItem.getImage();
        int screenWidth = UtilsDevice.getScreenWidth(context);
        Bitmap bitmap = UtilsMiscellaneous.getOptimallyScaledBitmap(image, screenWidth);
        if (bitmap != null) {
            holder.vImage.setImageBitmap(bitmap);
        } else {
            holder.vImage.setImageResource(R.drawable.ic_account_circle_white_64dp);
        }
    }

    /**
     * Fills the ViewHolder with the information about the drink.
     *
     * @param holder the ViewHolder to be filled.
     */
    private void bindDrinkHolder(DrinkHolder holder, int pos, int size) {
        holder.drinkItem = drinkItems.get(pos);
        DrinkSizeItem drinkSize = holder.drinkItem.getDrinkSizes().get(size);

        /* Name */
        holder.vName.setText(holder.drinkItem.getName());

        /* Icon */
//        holder.vIcon.setImageResource(holder.drinkItem.isVegetarian() ? R.drawable.carrot
//                                                                      : R.drawable.meat);

        /* Price */
        NumberFormat deDE = NumberFormat.getCurrencyInstance(Locale.GERMANY);
        holder.vPrice.setText(deDE.format(drinkSize.getPrice()));

        /* Size */
        holder.vSize.setText(context.getResources().getString(R.string.bierstube_menu_size_liters,
                                                              "" + drinkSize.getSize()));

        /* Image */
        byte[] image = holder.drinkItem.getImage();
        int screenWidth = UtilsDevice.getScreenWidth(context);
        Bitmap bitmap = UtilsMiscellaneous.getOptimallyScaledBitmap(image, screenWidth);
        if (bitmap != null) {
            holder.vImage.setImageBitmap(bitmap);
        } else {
            holder.vImage.setImageResource(R.drawable.ic_account_circle_white_64dp);
        }
    }

    /**
     * Return the size of your data-set (invoked by the layout manager).
     *
     * @return the number of items present.
     */
    @Override
    public int getItemCount() {
        return 1 + (mealOfTheDayItem == null ? 0 : 1) +
               1 + foodItems.size() +
               1 + numberOfDrinkCards();
    }

    /**
     * @param mealOfTheDayItem the new MealOfTheDayItem.
     * @param dailyMealItem    the new DailyMealItem.
     * @param foodItems        the new List of FoodItems.
     * @param drinkItems       the new List of DrinkItems.
     */
    public void setData(MealOfTheDayItem mealOfTheDayItem,
                        DailyMealItem dailyMealItem,
                        List<AbstractMetaItem<?>> foodItems,
                        List<AbstractMetaItem<?>> drinkItems,
                        List<AbstractMetaItem<?>> categoryItems) {
        this.mealOfTheDayItem = mealOfTheDayItem;
        this.dailyMealItem = dailyMealItem;

        /* prase FoodItems */
        this.foodItems = new ArrayList<>();
        for (AbstractMetaItem<?> foodItem : foodItems) {
            this.foodItems.add((FoodItem) foodItem);
        }

        /* parse CategoryItems */
        this.categoryItems = new ArrayList<>();
        for (AbstractMetaItem<?> categoryItem : categoryItems) {
            this.categoryItems.add((CategoryItem) categoryItem);
        }
        Collections.sort(this.categoryItems, (c1, c2) -> c1.getOrder() - c2.getOrder());

        /* parse DrinkItems */
        this.drinkItems = new ArrayList<>();
        for (AbstractMetaItem<?> drinkItem : drinkItems) {
            if (((DrinkItem) drinkItem).getDrinkSizes().size() > 0) {
                this.drinkItems.add((DrinkItem) drinkItem);
            }
        }

        /* calculate offsets */
        this.headlineSpecial = 0;
        this.startSpecial = 1;
        this.headlineFood = 1 + (this.mealOfTheDayItem == null ? 0 : 1);
        this.startFood = this.headlineFood + 1;
        this.headlineDrink = this.startFood + this.foodItems.size();
        this.startDrink = this.headlineDrink + 1;
    }

    /**
     * The ViewHolder for major headlines.
     */
    private class HeadlineHolder extends RecyclerView.ViewHolder {

        final TextView vTitle;

        HeadlineHolder(View view) {
            super(view);
            vTitle = (TextView) view.findViewById(R.id.headline_title);
        }
    }

    /**
     * The ViewHolder for daily meals.
     */
    private class DailyMealHolder extends RecyclerView.ViewHolder {

        MealOfTheDayItem mealOfTheDayItem;
        DailyMealItem dailyMealItem;

        final TextView vHeadline;
        final ImageView vIcon;
        final ImageView vImage;
        final TextView vName;
        final TextView vPrice;
        final TextView vCook;

        DailyMealHolder(View view) {
            super(view);

            view.setOnClickListener(v -> {
                Intent mealOfTheDayViewerIntent = new Intent(context,
                                                             MealOfTheDayViewerActivity.class);
                mealOfTheDayViewerIntent.setAction(Intent.ACTION_VIEW);
                mealOfTheDayViewerIntent.putExtra(
                        MealOfTheDayViewerFragment.MEAL_OF_THE_DAY_ITEM_KEY, mealOfTheDayItem);
                mealOfTheDayViewerIntent.putExtra(MealOfTheDayViewerFragment.DAILY_MEAL_KEY,
                                                  dailyMealItem);
                context.startActivity(mealOfTheDayViewerIntent);
            });

            vHeadline = (TextView) view.findViewById(R.id.meal_of_the_day_headline);
            vIcon = (ImageView) view.findViewById(R.id.meal_of_the_day_icon);
            vImage = (ImageView) view.findViewById(R.id.meal_of_the_day_image);
            vName = (TextView) view.findViewById(R.id.meal_of_the_day_title);
            vPrice = (TextView) view.findViewById(R.id.meal_of_the_day_price);
            vCook = (TextView) view.findViewById(R.id.meal_of_the_day_cook);
        }
    }

    /**
     * The ViewHolder for regular foods.
     */
    private class FoodHolder extends RecyclerView.ViewHolder {

        FoodItem foodItem;

        final TextView vName;
        final ImageView vIcon;
        final ImageView vImage;
        final TextView vPrice;

        FoodHolder(View view) {
            super(view);

            view.setOnClickListener(v -> {
                Intent foodViewerIntent = new Intent(context, FoodViewerActivity.class);
                foodViewerIntent.setAction(Intent.ACTION_VIEW);
                foodViewerIntent.putExtra(FoodViewerFragment.FOOD_ITEM_KEY, foodItem);
                context.startActivity(foodViewerIntent);
            });

            vName = (TextView) view.findViewById(R.id.food_title);
            vIcon = (ImageView) view.findViewById(R.id.food_icon);
            vImage = (ImageView) view.findViewById(R.id.food_image);
            vPrice = (TextView) view.findViewById(R.id.food_price);
        }
    }

    /**
     * The ViewHolder for regular drinks.
     */
    private class DrinkHolder extends RecyclerView.ViewHolder {

        DrinkItem drinkItem;

        final TextView vName;
        final ImageView vIcon;
        final ImageView vImage;
        final TextView vPrice;
        final TextView vSize;

        DrinkHolder(View view) {
            super(view);

            view.setOnClickListener(v -> {
                Intent drinkViewerIntent = new Intent(context, DrinkViewerActivity.class);
                drinkViewerIntent.setAction(Intent.ACTION_VIEW);
                drinkViewerIntent.putExtra(DrinkViewerFragment.DRINK_ITEM_KEY, drinkItem);
                context.startActivity(drinkViewerIntent);
            });

            vName = (TextView) view.findViewById(R.id.drink_title);
            vIcon = (ImageView) view.findViewById(R.id.drink_icon);
            vImage = (ImageView) view.findViewById(R.id.drink_image);
            vPrice = (TextView) view.findViewById(R.id.drink_price);
            vSize = (TextView) view.findViewById(R.id.drink_amount);
        }
    }
}
