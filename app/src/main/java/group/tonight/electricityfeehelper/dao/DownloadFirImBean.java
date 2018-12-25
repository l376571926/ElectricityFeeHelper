package group.tonight.electricityfeehelper.dao;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.List;

public class DownloadFirImBean implements Serializable {
    /**
     * auth : true
     * app : {"id":"5c21cd1e959d690ced45d505","type":"android","name":"电费收费小助手","desc":"","system_prefer_storage_name":"baidu","short":"b6wk","new_ad_tag":"","in_short_list":false,"wait_time":0,"is_opened":true,"has_passwd":false,"store_link_visible":true,"is_expired":false,"icon_url":"https://pro-icon-qn.fir.im/e8b6b1789643ebd5862b972e6215bf4d37a72e90?e=1545722943&token=LOvmia8oXF4xnLh0IdH05XMYpH6ENHNpARlmPc-T:EmDp4vesaK7URoiymYMnnvYYqVk=","token":"8d795a88f392ef390ebe4bdd610bf7a3","download_token_can_expired":false,"is_ad_app":false,"genre_id":0,"screenshots":[],"market_app_info":{"url":"","icon_url":"","screenshot_urls":[],"onlined_at":0},"releases":{"master":{"id":"5c21cd4d959d696e9fd92ece","version":"v1.0.7","build":"103","changelog":"初始版本 1.无自动更新功能 2.数据全部从tonight.group获取","is_history":false,"fsize":2072971,"created_at":1545719117,"release_type":"inhouse","is_onlined":false,"is_expired":false,"scan_virus":{"kingsoft":null,"baidu":null}},"history":[]},"constraint":false,"is_embedded_ad":false,"is_embedded_other":false,"is_embedded_caipiao":false}
     */

    private boolean auth;
    private AppBean app;

    public boolean isAuth() {
        return auth;
    }

    public void setAuth(boolean auth) {
        this.auth = auth;
    }

    public AppBean getApp() {
        return app;
    }

    public void setApp(AppBean app) {
        this.app = app;
    }

    public static class AppBean {
        /**
         * id : 5c21cd1e959d690ced45d505
         * type : android
         * name : 电费收费小助手
         * desc :
         * system_prefer_storage_name : baidu
         * short : b6wk
         * new_ad_tag :
         * in_short_list : false
         * wait_time : 0
         * is_opened : true
         * has_passwd : false
         * store_link_visible : true
         * is_expired : false
         * icon_url : https://pro-icon-qn.fir.im/e8b6b1789643ebd5862b972e6215bf4d37a72e90?e=1545722943&token=LOvmia8oXF4xnLh0IdH05XMYpH6ENHNpARlmPc-T:EmDp4vesaK7URoiymYMnnvYYqVk=
         * token : 8d795a88f392ef390ebe4bdd610bf7a3
         * download_token_can_expired : false
         * is_ad_app : false
         * genre_id : 0
         * screenshots : []
         * market_app_info : {"url":"","icon_url":"","screenshot_urls":[],"onlined_at":0}
         * releases : {"master":{"id":"5c21cd4d959d696e9fd92ece","version":"v1.0.7","build":"103","changelog":"初始版本 1.无自动更新功能 2.数据全部从tonight.group获取","is_history":false,"fsize":2072971,"created_at":1545719117,"release_type":"inhouse","is_onlined":false,"is_expired":false,"scan_virus":{"kingsoft":null,"baidu":null}},"history":[]}
         * constraint : false
         * is_embedded_ad : false
         * is_embedded_other : false
         * is_embedded_caipiao : false
         */

        private String id;
        private String type;
        private String name;
        private String desc;
        private String system_prefer_storage_name;
        @SerializedName("short")
        private String shortX;
        private String new_ad_tag;
        private boolean in_short_list;
        private int wait_time;
        private boolean is_opened;
        private boolean has_passwd;
        private boolean store_link_visible;
        private boolean is_expired;
        private String icon_url;
        private String token;
        private boolean download_token_can_expired;
        private boolean is_ad_app;
        private int genre_id;
        private MarketAppInfoBean market_app_info;
        private ReleasesBean releases;
        private boolean constraint;
        private boolean is_embedded_ad;
        private boolean is_embedded_other;
        private boolean is_embedded_caipiao;
        private List<?> screenshots;

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getType() {
            return type;
        }

        public void setType(String type) {
            this.type = type;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getDesc() {
            return desc;
        }

        public void setDesc(String desc) {
            this.desc = desc;
        }

        public String getSystem_prefer_storage_name() {
            return system_prefer_storage_name;
        }

        public void setSystem_prefer_storage_name(String system_prefer_storage_name) {
            this.system_prefer_storage_name = system_prefer_storage_name;
        }

        public String getShortX() {
            return shortX;
        }

        public void setShortX(String shortX) {
            this.shortX = shortX;
        }

        public String getNew_ad_tag() {
            return new_ad_tag;
        }

        public void setNew_ad_tag(String new_ad_tag) {
            this.new_ad_tag = new_ad_tag;
        }

        public boolean isIn_short_list() {
            return in_short_list;
        }

        public void setIn_short_list(boolean in_short_list) {
            this.in_short_list = in_short_list;
        }

        public int getWait_time() {
            return wait_time;
        }

        public void setWait_time(int wait_time) {
            this.wait_time = wait_time;
        }

        public boolean isIs_opened() {
            return is_opened;
        }

        public void setIs_opened(boolean is_opened) {
            this.is_opened = is_opened;
        }

        public boolean isHas_passwd() {
            return has_passwd;
        }

        public void setHas_passwd(boolean has_passwd) {
            this.has_passwd = has_passwd;
        }

        public boolean isStore_link_visible() {
            return store_link_visible;
        }

        public void setStore_link_visible(boolean store_link_visible) {
            this.store_link_visible = store_link_visible;
        }

        public boolean isIs_expired() {
            return is_expired;
        }

        public void setIs_expired(boolean is_expired) {
            this.is_expired = is_expired;
        }

        public String getIcon_url() {
            return icon_url;
        }

        public void setIcon_url(String icon_url) {
            this.icon_url = icon_url;
        }

        public String getToken() {
            return token;
        }

        public void setToken(String token) {
            this.token = token;
        }

        public boolean isDownload_token_can_expired() {
            return download_token_can_expired;
        }

        public void setDownload_token_can_expired(boolean download_token_can_expired) {
            this.download_token_can_expired = download_token_can_expired;
        }

        public boolean isIs_ad_app() {
            return is_ad_app;
        }

        public void setIs_ad_app(boolean is_ad_app) {
            this.is_ad_app = is_ad_app;
        }

        public int getGenre_id() {
            return genre_id;
        }

        public void setGenre_id(int genre_id) {
            this.genre_id = genre_id;
        }

        public MarketAppInfoBean getMarket_app_info() {
            return market_app_info;
        }

        public void setMarket_app_info(MarketAppInfoBean market_app_info) {
            this.market_app_info = market_app_info;
        }

        public ReleasesBean getReleases() {
            return releases;
        }

        public void setReleases(ReleasesBean releases) {
            this.releases = releases;
        }

        public boolean isConstraint() {
            return constraint;
        }

        public void setConstraint(boolean constraint) {
            this.constraint = constraint;
        }

        public boolean isIs_embedded_ad() {
            return is_embedded_ad;
        }

        public void setIs_embedded_ad(boolean is_embedded_ad) {
            this.is_embedded_ad = is_embedded_ad;
        }

        public boolean isIs_embedded_other() {
            return is_embedded_other;
        }

        public void setIs_embedded_other(boolean is_embedded_other) {
            this.is_embedded_other = is_embedded_other;
        }

        public boolean isIs_embedded_caipiao() {
            return is_embedded_caipiao;
        }

        public void setIs_embedded_caipiao(boolean is_embedded_caipiao) {
            this.is_embedded_caipiao = is_embedded_caipiao;
        }

        public List<?> getScreenshots() {
            return screenshots;
        }

        public void setScreenshots(List<?> screenshots) {
            this.screenshots = screenshots;
        }

        public static class MarketAppInfoBean {
            /**
             * url :
             * icon_url :
             * screenshot_urls : []
             * onlined_at : 0
             */

            private String url;
            private String icon_url;
            private int onlined_at;
            private List<?> screenshot_urls;

            public String getUrl() {
                return url;
            }

            public void setUrl(String url) {
                this.url = url;
            }

            public String getIcon_url() {
                return icon_url;
            }

            public void setIcon_url(String icon_url) {
                this.icon_url = icon_url;
            }

            public int getOnlined_at() {
                return onlined_at;
            }

            public void setOnlined_at(int onlined_at) {
                this.onlined_at = onlined_at;
            }

            public List<?> getScreenshot_urls() {
                return screenshot_urls;
            }

            public void setScreenshot_urls(List<?> screenshot_urls) {
                this.screenshot_urls = screenshot_urls;
            }
        }

        public static class ReleasesBean {
            /**
             * master : {"id":"5c21cd4d959d696e9fd92ece","version":"v1.0.7","build":"103","changelog":"初始版本 1.无自动更新功能 2.数据全部从tonight.group获取","is_history":false,"fsize":2072971,"created_at":1545719117,"release_type":"inhouse","is_onlined":false,"is_expired":false,"scan_virus":{"kingsoft":null,"baidu":null}}
             * history : []
             */

            private MasterBean master;
            private List<?> history;

            public MasterBean getMaster() {
                return master;
            }

            public void setMaster(MasterBean master) {
                this.master = master;
            }

            public List<?> getHistory() {
                return history;
            }

            public void setHistory(List<?> history) {
                this.history = history;
            }

            public static class MasterBean {
                /**
                 * id : 5c21cd4d959d696e9fd92ece
                 * version : v1.0.7
                 * build : 103
                 * changelog : 初始版本 1.无自动更新功能 2.数据全部从tonight.group获取
                 * is_history : false
                 * fsize : 2072971
                 * created_at : 1545719117
                 * release_type : inhouse
                 * is_onlined : false
                 * is_expired : false
                 * scan_virus : {"kingsoft":null,"baidu":null}
                 */

                private String id;
                private String version;
                private String build;
                private String changelog;
                private boolean is_history;
                private long fsize;
                private long created_at;
                private String release_type;
                private boolean is_onlined;
                private boolean is_expired;
                private ScanVirusBean scan_virus;

                public String getId() {
                    return id;
                }

                public void setId(String id) {
                    this.id = id;
                }

                public String getVersion() {
                    return version;
                }

                public void setVersion(String version) {
                    this.version = version;
                }

                public String getBuild() {
                    return build;
                }

                public void setBuild(String build) {
                    this.build = build;
                }

                public String getChangelog() {
                    return changelog;
                }

                public void setChangelog(String changelog) {
                    this.changelog = changelog;
                }

                public boolean isIs_history() {
                    return is_history;
                }

                public void setIs_history(boolean is_history) {
                    this.is_history = is_history;
                }

                public long getFsize() {
                    return fsize;
                }

                public void setFsize(long fsize) {
                    this.fsize = fsize;
                }

                public long getCreated_at() {
                    return created_at;
                }

                public void setCreated_at(long created_at) {
                    this.created_at = created_at;
                }

                public String getRelease_type() {
                    return release_type;
                }

                public void setRelease_type(String release_type) {
                    this.release_type = release_type;
                }

                public boolean isIs_onlined() {
                    return is_onlined;
                }

                public void setIs_onlined(boolean is_onlined) {
                    this.is_onlined = is_onlined;
                }

                public boolean isIs_expired() {
                    return is_expired;
                }

                public void setIs_expired(boolean is_expired) {
                    this.is_expired = is_expired;
                }

                public ScanVirusBean getScan_virus() {
                    return scan_virus;
                }

                public void setScan_virus(ScanVirusBean scan_virus) {
                    this.scan_virus = scan_virus;
                }

                public static class ScanVirusBean {
                    /**
                     * kingsoft : null
                     * baidu : null
                     */

                    private Object kingsoft;
                    private Object baidu;

                    public Object getKingsoft() {
                        return kingsoft;
                    }

                    public void setKingsoft(Object kingsoft) {
                        this.kingsoft = kingsoft;
                    }

                    public Object getBaidu() {
                        return baidu;
                    }

                    public void setBaidu(Object baidu) {
                        this.baidu = baidu;
                    }
                }
            }
        }
    }
}
