package uz.nt.ecommerce.bot;

import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import uz.nt.ecommerce.domain.dto.request.BasketCreateDto;
import uz.nt.ecommerce.domain.dto.request.OrderCreateDto;
import uz.nt.ecommerce.domain.dto.request.UserStateDto;
import uz.nt.ecommerce.domain.entity.*;
import uz.nt.ecommerce.domain.entity.enums.OrderStatus;
import uz.nt.ecommerce.domain.entity.enums.UserState;
import uz.nt.ecommerce.service.BasketService;
import uz.nt.ecommerce.service.OrderService;
import uz.nt.ecommerce.service.ProductService;
import uz.nt.ecommerce.service.UserService;

import java.util.List;

@Component
@RequiredArgsConstructor
public class ECommerceBot extends TelegramLongPollingBot {

    private final ECommerceBotService botService;

    @Override
    public String getBotUsername() {
        return "https://t.me/Savdogarg21_bot";
    }

    @Override
    public String getBotToken() {
        return "5955028809:AAFUslAqLAFUrcSWx-0PpZ5b_809BPFQOKE";
    }

    private final UserService userService;
    private final ProductService productService;
    private final OrderService orderService;
    private final BasketService basketService;

    private ProductEntity chosenProduct;
    private int amountPurchase;
    private UserEntity currentUser;
    private BasketEntity basketEdit;
    private OrderEntity currentOrder;

    @SneakyThrows
    @Override
    public void onUpdateReceived(Update update) {
        boolean balanceCheck = true;
        boolean amountCheck = true;
        if (update.hasMessage()) {
            Message message = update.getMessage();
            String chatId = message.getChatId().toString();
            String text = message.getText();
            if (!message.hasContact()) {
                switch (text) {
                    case "/start" -> {
                        execute(new SendMessage(chatId, "★ W E L C O M E ★"));
                        userService.updateState(new UserStateDto(chatId, UserState.REGISTERED));
                    }
                    case "\uD83D\uDCCB Categories" -> userService.updateState(new UserStateDto(chatId, UserState.CATEGORIES));
                    case "\uD83D\uDCD1 Orders" -> userService.updateState(new UserStateDto(chatId, UserState.ORDERS));
                    case "\uD83D\uDED2 Basket" -> userService.updateState(new UserStateDto(chatId, UserState.BASKET));
                }
                balanceCheck = text.matches("\\d+") && Double.parseDouble(text) > 0;
                amountCheck = text.matches("\\d+") && Integer.parseInt(text) > 0;
            }

            UserState userState = botService.checkState(chatId);
//            execute(new SendMessage(chatId, "Your State: " + userState.getStrName()));
            switch (userState) {
                case START -> {
                    if (message.hasContact()) {
                        execute(botService.registerUser(chatId, message.getContact()));
                    } else {
                        execute(botService.shareContact(chatId));
                    }
                }
                case REGISTERED -> execute(botService.showMainMenu(chatId));
                case CATEGORIES -> {
                    SendMessage catMessage = new SendMessage(chatId, "*CATEGORIES MENU* \uD83D\uDDD2️");
                    catMessage.setReplyMarkup(botService.keyboardRemove());
                    catMessage.setParseMode("Markdown");
                    execute(catMessage);
                    execute(botService.chooseProductType(chatId));
                }
                case ORDERS -> {
                    currentUser = userService.findByChatId(chatId);
                    StringBuilder orders = new StringBuilder("★★★★ *YOUR ORDERS* ★★★★");
                    List<OrderEntity> orderEntities = orderService.getMyOrders(currentUser.getId());
                    for (OrderEntity myOrder : orderEntities) {
                        orders.append("\n\n------------------------------");
                        orders.append("\n- *PRODUCT TYPE: *").append(productService.getById(myOrder.getProduct().getId()).getData().getCategory().getName());
                        orders.append("\n- *PRODUCT NAME: *").append(productService.getById(myOrder.getProduct().getId()).getData().getName());
                        orders.append("\n- *ORDER AMOUNT: *").append(myOrder.getAmount());
                        orders.append("\n- *ORDER TOTAL PRICE: *").append(myOrder.getTotalPrice());
                        orders.append("\n- *ORDER DATE: *").append(myOrder.getCreatedDate());
                        orders.append("\n- *ORDER STATUS: *").append(myOrder.getStatus());
                        orders.append("\n--------------------------------");
                    }
                    SendMessage orderMessage = new SendMessage(chatId, orders.toString());
                    orderMessage.setReplyMarkup(botService.keyboardRemove());
                    orderMessage.setParseMode("Markdown");
                    execute(orderMessage);
                    execute(botService.showMainMenu(chatId));
                }
                case BASKET -> {
                    currentUser = userService.findByChatId(chatId);
                    SendMessage basMessage = new SendMessage(chatId, "*YOUR BASKET MENU* \uD83E\uDDFA️");
                    basMessage.setReplyMarkup(botService.keyboardRemove());
                    basMessage.setParseMode("Markdown");
                    execute(basMessage);
                    execute(botService.myBasket(chatId, currentUser.getId()));
                }
            }
        } else if (update.hasCallbackQuery()) {
            CallbackQuery callbackQuery = update.getCallbackQuery();
            Message message = callbackQuery.getMessage();
            String data = callbackQuery.getData();
            String chatId = message.getChatId().toString();

            UserState userState = botService.checkState(chatId);
            if (userState == UserState.CATEGORIES) {
                CategoryEntity type = new CategoryEntity();
                type.setId(Long.valueOf(data));
                EditMessageText editCategories = new EditMessageText();
                editCategories.setMessageId(message.getMessageId());
                editCategories.setChatId(chatId);
                editCategories.setText("AVAILABLE CATEGORIES");
                editCategories.setReplyMarkup(botService.chooseCategoryChild(type));
                execute(editCategories);
                userService.updateState(new UserStateDto(chatId, UserState.CATEGORY_CHILD));
            } else if (userState == UserState.CATEGORY_CHILD) {
                CategoryEntity type = new CategoryEntity();
                type.setId(Long.valueOf(data));
                EditMessageText editCategories = new EditMessageText();
                editCategories.setMessageId(message.getMessageId());
                editCategories.setChatId(chatId);
                editCategories.setText("AVAILABLE PRODUCTS");
                editCategories.setReplyMarkup(botService.chooseCategoryProduct(type));
                execute(editCategories);
                userService.updateState(new UserStateDto(chatId, UserState.CATEGORY_PRODUCT));
            } else if (userState == UserState.CATEGORY_PRODUCT) {
                chosenProduct = productService.getById(Long.valueOf(data)).getData();
                execute(botService.basketAmount(chatId, message.getMessageId(), chosenProduct));
                userService.updateState(new UserStateDto(chatId, UserState.CATEGORY_PRODUCT_AMOUNT));
            } else if (userState == UserState.CATEGORY_PRODUCT_AMOUNT) {
                amountPurchase = 0;
                if (data.matches("\\d+")) {
                    amountPurchase = Integer.parseInt(data);
                    if (amountPurchase > chosenProduct.getQuantity()) {
                        SendMessage message1 = new SendMessage(chatId, "❌ *THIS AMOUNT OF THIS PRODUCT IS NOT AVAILABLE IN STORE!* ❌");
                        message1.setParseMode("Markdown");
                        execute(message1);
                    } else {
                        execute(botService.confirmPurchase(chatId, message.getMessageId(), chosenProduct, amountPurchase));
                        userService.updateState(new UserStateDto(chatId, UserState.CATEGORY_PRODUCT_CONFIRM));
                    }
                } else {
                    execute(new SendMessage(chatId, "❌ PLEASE CHOOSE CORRECT AMOUNT ❌"));
                }
            } else if (userState == UserState.CATEGORY_PRODUCT_CONFIRM) {
                EditMessageText editMessageText = new EditMessageText();
                editMessageText.setChatId(chatId);
                editMessageText.setMessageId(message.getMessageId());
                editMessageText.setParseMode("Markdown");
                if (data.equals("yes")) {
                    System.out.println(chatId);
                    currentUser = userService.findByChatId(chatId);
                    System.out.println(currentUser);
                    Double totalPrice = chosenProduct.getPrice() * amountPurchase;
                    System.out.println(currentUser.getUsername());
                    System.out.println(chosenProduct.getName());
                    System.out.println(amountPurchase);
                    System.out.println(totalPrice);
                    String addBasket = basketService.save(new BasketCreateDto(
                            currentUser,
                            chosenProduct,
                            amountPurchase,
                            totalPrice
                    )).getMessage();
                    System.out.println(addBasket);
                    chosenProduct.setQuantity(chosenProduct.getQuantity() - amountPurchase);
                    productService.updateQuantity(chosenProduct.getQuantity(), chosenProduct.getId());
                    editMessageText.setText("*" + addBasket + "*");
                    execute(editMessageText);
                } else {
                    editMessageText.setText("*CANCELLED* ☑");
                    execute(editMessageText);
                }
                execute(botService.showMainMenu(chatId));
            } else if (userState == UserState.BASKET) {
                switch (data) {
                    case "remove" -> {
                        currentUser = userService.findByChatId(chatId);
                        EditMessageText editMessageText = new EditMessageText();
                        editMessageText.setChatId(chatId);
                        editMessageText.setText(basketService.removeMyBasket(currentUser.getId()).getMessage());
                        editMessageText.setMessageId(message.getMessageId());
                        editMessageText.setParseMode("Markdown");
                        execute(editMessageText);
                        execute(botService.showMainMenu(chatId));
                    }
                    case "order" -> {
                        List<BasketEntity> basketEntities = basketService.getMyBaskets(currentUser.getId());
                        for (BasketEntity myBasket : basketEntities) {
                            orderService.save(new OrderCreateDto(
                                    myBasket.getProduct(),
                                    myBasket.getUser(),
                                    myBasket.getAmount(),
                                    myBasket.getTotalPrice(),
                                    OrderStatus.CREATED
                            ));
                        }
                        currentUser = userService.findByChatId(chatId);
                        EditMessageText editMessageText = new EditMessageText();
                        editMessageText.setChatId(chatId);
                        editMessageText.setText("SUCCESSFULLY ORDERED ☑");
                        editMessageText.setMessageId(message.getMessageId());
                        editMessageText.setParseMode("Markdown");
                        execute(editMessageText);
                        execute(botService.showMainMenu(chatId));
                    }
                    case "back" -> {
                        EditMessageText editMessageText = new EditMessageText();
                        editMessageText.setChatId(chatId);
                        editMessageText.setText("RETURNED TO PREVIOUS MENU \uD83D\uDD19");
                        editMessageText.setMessageId(message.getMessageId());
                        editMessageText.setParseMode("Markdown");
                        editMessageText.setReplyMarkup(null);
                        execute(editMessageText);
                        execute(botService.showMainMenu(chatId));
                    }
                    case "back edit" -> {
                        EditMessageText editMessageText = new EditMessageText();
                        editMessageText.setChatId(chatId);
                        editMessageText.setText(botService.myBasket(chatId, currentUser.getId()).getText());
                        editMessageText.setMessageId(message.getMessageId());
                        editMessageText.setParseMode("Markdown");
                        editMessageText.setReplyMarkup(botService.basketInline(currentUser.getId()));
                        execute(editMessageText);
                    }
                    case "remove basket" -> {
                        basketService.delete(basketEdit.getId());
                        EditMessageText editMessageText = new EditMessageText();
                        editMessageText.setChatId(chatId);
                        editMessageText.setText(botService.myBasket(chatId, currentUser.getId()).getText());
                        editMessageText.setMessageId(message.getMessageId());
                        editMessageText.setParseMode("Markdown");
                        editMessageText.setReplyMarkup(botService.basketInline(currentUser.getId()));
                        execute(editMessageText);
                    }
                    default -> {
                        basketEdit = basketService.getById(Long.valueOf(data)).getData();
                        EditMessageText editMessageText = new EditMessageText();
                        editMessageText.setChatId(chatId);
                        editMessageText.setText("****** BASKET INFO ******" +
                                "\nPRODUCT TYPE: " + productService.getById(basketEdit.getProduct().getId()).getData().getCategory().getName() +
                                "\nPRODUCT NAME: " + productService.getById(basketEdit.getProduct().getId()).getData().getName() +
                                "\nSELECTED AMOUNT: " + basketEdit.getAmount() +
                                "\nTOTAL PRICE: " + basketEdit.getTotalPrice());
                        editMessageText.setMessageId(message.getMessageId());
                        editMessageText.setParseMode("Markdown");
                        editMessageText.setReplyMarkup(botService.basketEdit());
                        execute(editMessageText);
                    }
                }
            }
        }
    }
}