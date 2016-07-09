function config()
{
    echo $1 $2 $3
    rm -f "$2"/settings.gradle
    cd $2; ln -sf settings.gradle."$1" settings.gradle; cd -

    rm -f $2/$3/build.gradle
    cd $2/$3; ln -sf build.gradle.$1 build.gradle; cd -
}

function configs() {
    config $1 "PPComDemo" "app"
    config $1 "PPComSDK" "ppcomsdk"
}

function config_help() {
    echo "build-config [local] [jcenter]"
}

case "$1" in

    local)
        configs "local"
        ;;

    jcenter)
        configs "jcenter"
        ;;

    *)
        config_help
        exit 0
        ;;
esac

