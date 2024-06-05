use crate::node::Node;
use crate::token::Token;

pub fn parse<'a>(tokens: &Vec<Token>) -> &'a Node<'a> {
    parse_expression(tokens, &mut 0).unwrap()
}

fn parse_expression<'a>(tokens: &Vec<Token>, i: &mut usize) -> Option<&'a Node<'a>> {
    if *i >= tokens.len() {
        return None;
    }

    let n: &Node = match &tokens[*i] {
        Token::IntegerLiteral(x) => &Node::Constant { value: *x },
        Token::Name(y) => {
            if *i + 1 < tokens.len() && tokens[*i + 1] == Token::LeftBracket {
                *i += 1;
                match parse_bracketed_expression(tokens, i) {
                    Node::Bracket { expr } => &Node::Function {
                        name: y.to_string(),
                        expr: expr,
                    },
                    _ => todo!(),
                }
            } else {
                &Node::Variable {
                    name: y.to_string(),
                }
            }
        }
        Token::LeftBracket => &parse_bracketed_expression(tokens, i),
        _ => todo!(),
    };

    *i += 1;
    if *i < tokens.len() && tokens[*i] == Token::Plus {
        *i += 1;
        return Some(&Node::Plus {
            lhs: &n,
            rhs: &parse_expression(tokens, i).unwrap(),
        });
    } else if *i < tokens.len() && tokens[*i] == Token::Minus {
        *i += 1;
        return Some(&Node::Minus {
            lhs: &n,
            rhs: &parse_expression(tokens, i).unwrap(),
        });
    } else if *i < tokens.len() && tokens[*i] == Token::Asterisk {
        *i += 1;
        return Some(&Node::Multiply {
            lhs: &n,
            rhs: &parse_expression(tokens, i).unwrap(),
        });
    } else if *i < tokens.len() && tokens[*i] == Token::Slash {
        *i += 1;
        return Some(&Node::Divide {
            lhs: &n,
            rhs: &parse_expression(tokens, i).unwrap(),
        });
    } else if *i < tokens.len() && tokens[*i] == Token::Circumflex {
        *i += 1;
        return Some(&Node::Power {
            lhs: &n,
            rhs: &parse_expression(tokens, i).unwrap(),
        });
    } else {
        return Some(n);
    }
}

fn parse_bracketed_expression<'a>(tokens: &Vec<Token>, i: &mut usize) -> &'a Node<'a> {
    if tokens[*i] == Token::LeftBracket {
        *i += 1;
        let n: &Node = parse_expression(tokens, i).unwrap();
        if tokens[*i] != Token::RightBracket {
            panic!("Expected right bracket");
        }
        &Node::Bracket { expr: n }
    } else {
        parse_expression(tokens, i).unwrap()
    }
}

#[cfg(test)]
mod tests {
    use super::*;
    use test_case::test_case;

    #[test_case(
        &vec![Token::LeftBracket,Token::IntegerLiteral(3),Token::RightBracket],
        Node::Bracket{expr:&Node::Constant{value:3}} ; "(3)")]
    #[test_case(
        &vec![Token::LeftBracket,Token::Name("abc".to_string()),Token::RightBracket],
        Node::Bracket{expr:&Node::Variable{name:"abc".to_string()}} ; "(abc)")]
    #[test_case(
        &vec![Token::Name("x".to_string()),Token::Plus,Token::IntegerLiteral(2)],
        Node::Plus{lhs:&Node::Variable{name:"x".to_string()}, rhs:&Node::Constant{value:2}} ; "x_plus_2")]
    #[test_case(
        &vec![Token::Name("x".to_string()),Token::Minus,Token::IntegerLiteral(2)],
        Node::Minus{lhs:&Node::Variable{name:"x".to_string()}, rhs:&Node::Constant{value:2}} ; "x_minus_2")]
    #[test_case(
        &vec![Token::Name("x".to_string()),Token::Asterisk,Token::IntegerLiteral(2)],
        Node::Multiply{lhs:&Node::Variable{name:"x".to_string()},rhs: &Node::Constant{value:2}} ; "x_times_2")]
    #[test_case(
        &vec![Token::Name("x".to_string()),Token::Slash,Token::IntegerLiteral(2)],
        Node::Divide{lhs:&Node::Variable{name:"x".to_string()},rhs: &Node::Constant{value:2}} ; "x_divide_2")]
    #[test_case(
        &vec![Token::Name("x".to_string()),Token::Circumflex,Token::IntegerLiteral(2)],
        Node::Power{lhs:&Node::Variable{name:"x".to_string()}, rhs:&Node::Constant{value:2}} ; "x_power_2")]
    #[test_case(
        &vec![Token::LeftBracket,Token::IntegerLiteral(1),Token::Plus,Token::IntegerLiteral(2),Token::RightBracket,Token::Plus,Token::IntegerLiteral(3)],
        Node::Plus{
            lhs: &Node::Bracket{
                expr: &Node::Plus{
                    lhs: &Node::Constant{value:1},
                    rhs: &Node::Constant{value:2}
                }
            },
            rhs: &Node::Constant{value:3}
        } ; "(1+2)+3")]
    #[test_case(
        &vec![Token::Name("sin".to_string()),Token::LeftBracket,Token::Name("x".to_string()),Token::RightBracket],
        Node::Function{name:"sin".to_string(),expr:&Node::Variable{name:"x".to_string()}} ; "sin(x)")]
    fn parse_brackets(input: &Vec<Token>, output: Node) {
        assert_eq!(*parse(input), output);
    }
}
