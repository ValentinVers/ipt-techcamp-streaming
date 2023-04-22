az login

echo "Delete topics..."
az eventhubs eventhub list --resource-group rg-techcamp-streaming --namespace-name techcamp | grep \"name\" | while read -r line ; do
    without_prefix=${line#"\"name\": \""}
    without_postfix=${without_prefix%\",}
    # Make exception for these topics
    if [ $without_postfix == "email2" -o $without_postfix == "demo" -o $without_postfix == "demo-related" ]; then
        echo "Don't delete topic $without_postfix"
    else
        echo "Delete topic $without_postfix"
        az eventhubs eventhub delete --resource-group rg-techcamp-streaming --namespace-name techcamp --name $without_postfix
    fi
done;