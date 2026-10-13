package com.zozulia.nanovwap.client;

import com.zozulia.nanovwap.dto.Asset;
import org.springframework.web.client.RestClient;

import java.util.ArrayList;
import java.util.List;

public class AlpacaAssetClient {

    private final RestClient restClient;

    public AlpacaAssetClient(String url, String key, String secret) {
        restClient = RestClient
                .builder()
                .baseUrl(url)
                .defaultHeader("APCA-API-KEY-ID", key)
                .defaultHeader("APCA-API-SECRET-KEY",secret)
                .build();
    }

    public List<String> getAssets(String assetClass) {
        Asset[] assets = restClient.get()
                .uri("/v2/assets?status=active&asset_class={assetClass}", assetClass)
                .retrieve()
                .body(Asset[].class);

        List<String> assetsList = new ArrayList<>();
        for (Asset asset : assets) {
            if(asset.tradable()){
                assetsList.add(asset.symbol());
            }
        }
        return assetsList;
    }
}
