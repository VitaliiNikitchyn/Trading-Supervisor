package com.tradingsupervisor.viewmodel;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.ViewModel;

import com.tradingsupervisor.database.entity.Assortment;
import com.tradingsupervisor.database.entity.Shop;

import java.util.ArrayList;
import java.util.List;

public class ShopViewModel extends ViewModel {

    private MutableLiveData<List<Shop>> shopList;

    public ShopViewModel() {
        shopList = new MutableLiveData<>();
        shopList.setValue(genData());
    }

    public LiveData<List<Shop>> getShops() {
        return shopList;
    }

    public List<Assortment> getAssortment(int index) {
        return shopList.getValue().get(index).getAssortment();
    }


    //move to repository
    private List<Shop> genData() {
        String shopName[] = {"ATB", "Billa", "ATB"}; //groupItems name
        String shopAddress[] = {
                "проспект Миру, 15, Житомир, Житомирська область, 10001",
                "проспект Миру, 15, Житомир, Житомирська область, 10000",
                "вулиця Житній Базар, 6, Житомир, Житомирська область, 10002"};
        String[][] assortmentItems = {
                {"Gold Key", "Super Schocolate", "Plombir 100%"},
                {"Gold Key", "Super Schocolate", "assortment 3"}, {"test1", "Super Schocolate", "Plombir 100%"}};

        List<Shop> list = new ArrayList<>();

        List<Assortment> assortmentList1 = new ArrayList<>();
        assortmentList1.add(new Assortment(assortmentItems[0][0]));
        assortmentList1.add(new Assortment(assortmentItems[0][1]));
        assortmentList1.add(new Assortment(assortmentItems[0][2]));

        Shop shop1 = new Shop();
        shop1.setId(3);
        shop1.setName(shopName[0]);
        shop1.setAddress(shopAddress[0]);
        shop1.setAssortment(assortmentList1);


        List<Assortment> assortmentList2 = new ArrayList<>();
        assortmentList2.add(new Assortment(assortmentItems[1][0]));
        assortmentList2.add(new Assortment(assortmentItems[1][1]));
        assortmentList2.add(new Assortment(assortmentItems[1][2]));
        Shop shop2 = new Shop();
        shop2.setId(4);
        shop2.setName(shopName[1]);
        shop2.setAddress(shopAddress[1]);
        shop2.setAssortment(assortmentList2);


        List<Assortment> assortmentList3 = new ArrayList<>();
        assortmentList3.add(new Assortment(assortmentItems[2][0]));
        assortmentList3.add(new Assortment(assortmentItems[2][1]));
        assortmentList3.add(new Assortment(assortmentItems[2][2]));

        Shop shop3 = new Shop();
        shop3.setId(6);
        shop3.setName(shopName[2]);
        shop3.setAddress(shopAddress[2]);
        shop3.setAssortment(assortmentList3);

        list.add(shop1);
        list.add(shop2);
        list.add(shop3);
        list.add(shop3);

        return list;
    }
}
