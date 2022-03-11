import 'package:flutter/material.dart';
import 'package:hooks_riverpod/hooks_riverpod.dart';
import 'package:olydorf/global/consts.dart';
import 'package:olydorf/providers/auth_provider.dart';
import 'package:olydorf/providers/events_provider.dart';
import 'package:olydorf/views/auth/login_view.dart';
import 'package:olydorf/views/auth/sign_up_view.dart';
import 'package:olydorf/views/bottom_navigation_bar/bottom_navigation_bar_view.dart';
import 'package:olydorf/views/chat/chat_view.dart';
import 'package:olydorf/views/welcome/welcome_view.dart';

class App extends StatefulHookConsumerWidget {
  const App({Key? key}) : super(key: key);

  @override
  _AppState createState() => _AppState();
}

class _AppState extends ConsumerState<App> {
  final _navigatorKey = GlobalKey<NavigatorState>();

  Future<void> _init(WidgetRef ref) async {
    // load auth state
    final user = ref.read(userProvider);
    if (user != null) {
      final userData = await ref.read(userDataProvider).getCurrentUser();
      ref.read(currentUserProvider.state).update((user) => user = userData);

      ref.read(userLoggedInProvider.state).state = true;
    } else {
      ref.read(userLoggedInProvider.state).state = false;
    }

    // load events
    ref.read(eventsListProvider.notifier).getEvents();
  }

  @override
  void initState() {
    super.initState();
    _init(ref);
  }

  @override
  Widget build(BuildContext context) {
    return MaterialApp(
      home: const WelcomeView(),
      navigatorKey: _navigatorKey,
      debugShowCheckedModeBanner: false,
      routes: {
        Routes.login: (context) => LoginView(),
        Routes.signUp: (context) => SignUpView(),
        Routes.bottomNavigationBar: (context) =>
            const BottomNavigationBarView(),
      },
    );
  }
}
